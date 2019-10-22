package com.example.dsawyer.maddscore.ScoreCards;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.R;

public class CardParSelectorFragment  extends BottomSheetDialogFragment implements View.OnClickListener{
    private static final String TAG = "TAG";

    private Button par2, par3, par4, par5, par6;
    private static final int PAR2 = 2;
    private static final int PAR3 = 3;
    private static final int PAR4 = 4;
    private static final int PAR5 = 5;
    private static final int PAR6 = 6;

    public interface OnParSelectedListener {
        void onParSelected(int par);
    }
    public OnParSelectedListener parSelected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_par_selector, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        par2 = view.findViewById(R.id.par_2);
        par2.setOnClickListener(this);
        par3 = view.findViewById(R.id.par_3);
        par3.setOnClickListener(this);
        par4 = view.findViewById(R.id.par_4);
        par4.setOnClickListener(this);
        par5 = view.findViewById(R.id.par_5);
        par5.setOnClickListener(this);
        par6 = view.findViewById(R.id.par_6);
        par6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.par_2):
                parSelected.onParSelected(PAR2);
                getDialog().dismiss();
                break;
            case (R.id.par_3):
                parSelected.onParSelected(PAR3);
                getDialog().dismiss();
                break;
            case (R.id.par_4):
                parSelected.onParSelected(PAR4);
                getDialog().dismiss();
                break;
            case (R.id.par_5):
                parSelected.onParSelected(PAR5);
                getDialog().dismiss();
                break;
            case (R.id.par_6):
                parSelected.onParSelected(PAR6);
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
           parSelected = (OnParSelectedListener) getActivity();
        }
        catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
