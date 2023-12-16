package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.databinding.PageBinding;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.SectionAdapter;

public class AgendaEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_AGENDA_ID = "agendaId";
    public static final String EXTRA_ACTIVL_ID = "activityLId";

    private Agenda agenda;
    private AlertDialog dialog;
    private long activityLId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;

        long agendaId = getArguments().getLong(EXTRA_AGENDA_ID, -1);
        activityLId = getArguments().getLong(EXTRA_ACTIVL_ID, -1);
        if (agendaId != -1)
            agenda = AppModule.retrieveAgendaUseCases().getAgendaUseCase.invoke(agendaId);
        else
            agenda = Agenda.empty();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        PageBinding binding = PageBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind(PageBinding binding) {
        binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        binding.img.setImageResource(R.drawable.blur_v1);
        binding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        binding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );

        associateTitle(binding.pageTitle);
        binding.pageTitle.setOnKeyListener((v, keyCode, event)->keyCode == KeyEvent.KEYCODE_ENTER);

        RecyclerView recyclerView = binding.pageSections;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        final SectionAdapter adapter = new SectionAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setScrollToFunc((oid, id)-> scrollTo(id, recyclerView));
        adapter.setBinaryLabel(
                new SectionAdapter.Label(
                        binding.pageYellowLabel,
                        binding.pageYellowLabelText,
                        binding.pageYellowLabelIcon
                ),
                new SectionAdapter.Label(
                        binding.pageYellowLabel2,
                        binding.pageYellowLabel2Text,
                        binding.pageYellowLabel2Icon
                )
        );
        int expandId = adapter.setAgenda(agenda, activityLId);
        recyclerView.post(()-> scrollTo(expandId, recyclerView));
    }

    public void scrollTo(int pos, RecyclerView recyclerView) {
        Context rContext = recyclerView.getContext();
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rContext){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(pos > 0 ? pos-1 : 0);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null)
            layoutManager.startSmoothScroll(smoothScroller);
    }

    public void associateTitle(EditText editText) {
        editText.setText(agenda.agenda.title);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                agenda.agenda.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        if (agenda.agenda.title.isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        saveAgenda();
        dialog.dismiss();
    }
    public void onCancel() {
        dialog.dismiss();
    }

    public void saveAgenda() {
        AppModule.retrieveAgendaUseCases()
                .putAgendaUseCase.invoke(agenda);
    }
}

