package org.smartregister.chw.harmreduction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.model.HarmReductionMobilizationModel;

import java.util.List;

public class HarmReductionMobilizationRegisterAdapter extends RecyclerView.Adapter<HarmReductionMobilizationRegisterAdapter.HarmReductionMobilzationViewHolder> {

    private final Context context;
    private final List<HarmReductionMobilizationModel> tbLeprosyMobilizationModels;


    public HarmReductionMobilizationRegisterAdapter(List<HarmReductionMobilizationModel> tbLeprosyMobilizationModels, Context context) {
        this.tbLeprosyMobilizationModels = tbLeprosyMobilizationModels;
        this.context = context;
    }

    @NonNull
    @Override
    public HarmReductionMobilzationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View followupLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tbleprosy_mobilization_session_card_view, viewGroup, false);
        return new HarmReductionMobilzationViewHolder(followupLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull HarmReductionMobilzationViewHolder holder, int position) {
        HarmReductionMobilizationModel tbLeprosyMobilizationModel = tbLeprosyMobilizationModels.get(position);
        holder.bindData(tbLeprosyMobilizationModel);
    }

    @Override
    public int getItemCount() {
        return tbLeprosyMobilizationModels.size();
    }

    protected class HarmReductionMobilzationViewHolder extends RecyclerView.ViewHolder {
        public TextView mobilizationSessionDate;
        public TextView mobilizationSessionParticipants;
//        public TextView mobilizationSessionCondomsIssued;

        public HarmReductionMobilzationViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(HarmReductionMobilizationModel tbLeprosyMobilizationModel) {
            mobilizationSessionDate = itemView.findViewById(R.id.mobilization_session_date);
            mobilizationSessionParticipants = itemView.findViewById(R.id.mobilization_session_participants);
//            mobilizationSessionCondomsIssued = itemView.findViewById(R.id.mobilization_session_condoms_issued);

            mobilizationSessionDate.setText(context.getString(R.string.mobilziation_session_date, tbLeprosyMobilizationModel.getSessionDate()));
            mobilizationSessionParticipants.setText(context.getString(R.string.mobilization_session_participants, tbLeprosyMobilizationModel.getSessionParticipants()));
//            mobilizationSessionCondomsIssued.setText(context.getString(R.string.mobilization_condoms_issued, tbLeprosyMobilizationModel.getCondomsIssued()));
        }
    }
}
