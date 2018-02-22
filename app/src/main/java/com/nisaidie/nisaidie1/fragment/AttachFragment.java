/*
package com.nisaidie.nisaidie1.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.adapters.Adapter;

*/
/**
 * Created by IT on 8/4/2017.
 *//*


public class AttachFragment extends Fragment {
    //GridView attachButton;
    GridView gridView;
    ImageView share;
    ImageView video;
    ImageView camera;
    ImageView gallery;
    Button cancel;
    String[] nisaidie_categories = {"Gallery", "Share", "Camera", "Video"};
    int[] categoriesImages;
    Adapter adapter;
    Context curContext;

    //animated menu
    LinearLayout mRevealView;
    boolean hidden = true;






    public static AttachFragment newInstance(String[] categories) {
        AttachFragment frag = new AttachFragment();
        return frag;
    }


    public AttachFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.attach_popup, container, false);

        mRevealView = (LinearLayout) getActivity().findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        //attachButton = (GridView) rootView.findViewById(R.id.attach_layout);
       */
/* share = (ImageView) rootView.findViewById(R.id.share);
        video = (ImageView) rootView.findViewById(R.id.video);
        camera = (ImageView) rootView.findViewById(R.id.camera);
        gallery = (ImageView) rootView.findViewById(R.id.gallery);*//*
 //old stuff

       // curContext = getActivity().getApplicationContext(); //old stuff
      //  getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //old stuff

        //new CategoryLoader().execute();

        return rootView;
    }


    private class CategoryLoader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new Adapter(getActivity(), nisaidie_categories, categoriesImages);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //bar.setVisibility(View.GONE);
            setGridView();

        }
    }


    private void setGridView() {

        getDialog().setTitle("Select Categories");
       gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(), nisaidie_categories[i], Toast.LENGTH_SHORT).show();
                Log.i("selected Item", i + "");

                int p = 0;
                chooseCategory(p);
                dismiss();
            }
        });
    }


    private void chooseCategory(int flag) {
        switch (flag) {
            case 0:
                Toast.makeText(getActivity(), "hwllo", Toast.LENGTH_SHORT).show();
                //sendSMSMessage();
                break;
            case 1:
                Toast.makeText(getActivity(), "hwldgdfdlo", Toast.LENGTH_SHORT).show();
                //sendSMSMessage();
                break;
            case 2:
                Toast.makeText(getActivity(), "hwdfgdfgdllo", Toast.LENGTH_SHORT).show();
               // sendSMSMessage();
                break;
        }
    }
}
*/
