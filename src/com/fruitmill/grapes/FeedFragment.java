package com.fruitmill.grapes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedFragment extends Fragment {

	private String[] remoteVideoList = {
            "https://app.box.com/shared/static/0354t4weyk08em4394y9.mp4",
            "https://app.box.com/shared/static/yucc83pdqqxzbzdke30p.mp4",
            "https://app.box.com/shared/static/nl0d20l3fuh4l0drfyrf.mp4",
            "https://app.box.com/shared/static/u9yy4jmwqy23d0qz5qyp.mp4",
            "https://app.box.com/shared/static/8s5ptkto96622yll63qy.mp4"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
		
		return rootView;
	}
	
}
