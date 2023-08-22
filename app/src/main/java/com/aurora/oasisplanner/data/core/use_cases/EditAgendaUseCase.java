package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.AgendaEditDialog;

public class EditAgendaUseCase {
    private AgendaRepository repository;
    private FragmentManager fragmentManager;

    public EditAgendaUseCase(AgendaRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /** -1 indicates a new agenda. */
    public void invoke(long agendaId) {
        if (fragmentManager == null)
            throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");
        AgendaEditDialog dialog = new AgendaEditDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(AgendaEditDialog.EXTRA_AGENDA_ID, agendaId);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "myDialog");
    }
}
