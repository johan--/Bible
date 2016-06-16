package com.papa.bible;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class ProgressContentDialogFragment extends DialogFragment {

	private TextView mContentText;

	public static void showProgress(FragmentManager manager, String content) {

		ProgressContentDialogFragment fragment = (ProgressContentDialogFragment) manager.findFragmentByTag(ProgressContentDialogFragment.class.getSimpleName());
		if (fragment != null) {
			manager.beginTransaction().remove(fragment).commit();
		}
		fragment = new ProgressContentDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("content", content);
		fragment.setArguments(bundle);
		fragment.show(manager, ProgressContentDialogFragment.class.getSimpleName());

	}

	/**
	 * 
	 * @param manager
	 * @param content
	 * @param type
	 *            == 1;不可点击
	 */
	public static void showProgress(FragmentManager manager, String content, int type) {

		ProgressContentDialogFragment fragment = (ProgressContentDialogFragment) manager.findFragmentByTag(ProgressContentDialogFragment.class.getSimpleName());
		if (fragment != null) {
			manager.beginTransaction().remove(fragment).commit();
		}
		fragment = new ProgressContentDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("content", content);
		bundle.putInt("type", type);

		fragment.setArguments(bundle);
		fragment.show(manager, ProgressContentDialogFragment.class.getSimpleName());

	}

	public static void showProgress(FragmentManager manager) {
		showProgress(manager, null);
	}

	public static void dismissProgress(FragmentManager manager) {
		if (manager == null)
			return;
		ProgressContentDialogFragment fragment = (ProgressContentDialogFragment) manager.findFragmentByTag(ProgressContentDialogFragment.class.getSimpleName());
		if (fragment != null && fragment.isAdded()) {
			fragment.dismissAllowingStateLoss();
		}
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
	}

	public void setContent(String content) {
		mContentText.setText(content);
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_progress_content_dialog, container, false);
		mContentText = (TextView) view.findViewById(R.id.content_text);
		if (getArguments() != null) {
			mContentText.setText(getArguments().getString("content"));

			if (getArguments().getInt("type", -1) == 1) {
				this.setCancelable(false);
			}
		}

		return view;
	}

}
