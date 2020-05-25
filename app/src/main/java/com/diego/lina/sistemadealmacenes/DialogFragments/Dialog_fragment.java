package com.diego.lina.sistemadealmacenes.DialogFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapterConsulta;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapterConsultaMensaje;
import com.diego.lina.sistemadealmacenes.MainActivity;
import com.diego.lina.sistemadealmacenes.R;
import com.diego.lina.sistemadealmacenes.Reportes2Fragment;
import com.diego.lina.sistemadealmacenes.principal_pagina_menu;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Dialog_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dialog_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dialog_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btn_aceptar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListaImagenesAdapterConsultaMensaje listaImagenesAdapterConsulta;
    public Dialog_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dialog_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Dialog_fragment newInstance(String param1, String param2) {
        Dialog_fragment fragment = new Dialog_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaImagenesAdapterConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reportes2Fragment rp = new Reportes2Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedor, rp);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        Log.i("AVISO", "AVISO");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragment = inflater.inflate(R.layout.fragment_dialog_fragment, container, false);

        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
