package ru.sigmadigital.inchat.fragments.newW.orders;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.customviews.IdCheckBox;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.utils.DpPxUtil;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ChooseCategoryDialog extends DialogFragment {
    private OnCategoryClick listener;
    private LinearLayout ll;

    public static ChooseCategoryDialog newInstance(OnCategoryClick listener){
        ChooseCategoryDialog dialog = new ChooseCategoryDialog();
        dialog.listener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ll = (LinearLayout) LayoutInflater.from(App.getAppContext()).inflate(R.layout.blank_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(ll);

        getOrdersCategories();
        return builder.create();
    }

    private void getOrdersCategories() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                App.getRetrofitService().getOrdersCategories(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<Category>>() {

                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        Log.e("onResponseCode", response.code() + " ");
                        if (response.body() != null) {
                            for (final Category category : response.body()) {
                                Log.e("onResponse", " " + category.toJson());
                                TextView textView = new TextView(App.getAppContext());
                                textView.setText(category.getName());
                                textView.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.bottomMargin = DpPxUtil.getPixelsFromDp(16);
                                ll.addView(textView, params);

                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        listener.onCategoryClick(category);
                                        dismiss();
                                    }
                                });
                            }

                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    ErrorHandler.catchError(error, getActivity());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    interface OnCategoryClick{
        void onCategoryClick(Category c);
    }
}
