package com.easyshare.fragment.album;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.easyshare.R;
import com.easyshare.activity.ClassificationActivity;
import com.easyshare.activity.LoginActivity;
import com.easyshare.adapter.PublishImageAdapter;
import com.easyshare.base.BaseException;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.base.ValueCallBack;
import com.easyshare.entity.ClassificationEntity;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.FileUtils;
import com.easyshare.utils.GlideEngine;
import com.easyshare.utils.OSSUtils;
import com.easyshare.utils.UserUtils;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.BitmapUtils;
import com.luck.picture.lib.utils.DateUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class PublishImageTextFragment extends BaseFragment implements PublishImageAdapter.OnItemClickListener, CropEngine {

    private PublishImageTextViewModel mViewModel;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    PublishImageAdapter mImageAdapter;
    PictureSelectorStyle selectorStyle;

    @BindView(R.id.et_title)
    EditText mTitleEditText;
    @BindView(R.id.classification_text)
    TextView mClassificationTextView;

    @BindView(R.id.display_style_complex)
    RadioButton mComplexStyleRadioButton;

    @BindView(R.id.share_btn)
    Button mShareButton;

    // ?????????????????????
    ClassificationEntity mClassificationEntity;
    ActivityResultLauncher<Intent> mClassificationResultLauncher;
    ValueCallBack<ActivityResult> mClassificationResultCallBack;


    public static PublishImageTextFragment newInstance() {
        return new PublishImageTextFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish_image_text, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassificationResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (mClassificationResultCallBack != null)
                        mClassificationResultCallBack.onSuccess(result);
                });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PublishImageTextViewModel.class);
        // init view
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mImageAdapter = new PublishImageAdapter(getContext());
        mRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.setOnItemClickListener(this);
        // ??????????????????
        selectorStyle = buildPictureSelectorOptions();
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(6) // ????????????????????????
                .setSelectorUIStyle(selectorStyle) // ??????????????????
                .setCropEngine(this) // ??????
                .forResult(new OnResultCallbackListener<LocalMedia>() { // ???????????????????????????
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        mImageAdapter.getData().addAll(result);
                        mImageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {
                        getActivity().finish();
                    }
                });
    }


    /**
     * ??????????????????
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_publish_nav_bottom, menu);
    }

    /**
     * ?????????????????? -- ??????
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.nav_publish == item.getItemId()) {
            onPublishClick();
            return true;
        } else return super.onOptionsItemSelected(item);
    }


    /**
     * ??????????????????
     */
    @OnClick(R.id.share_btn)
    public void onPublishClick() {
        mShareButton.setEnabled(false);
        // ????????????
        String title = mTitleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show(R.string.publish_image_text_title_hint);
            mShareButton.setEnabled(true);
            return;
        }
        if (mClassificationEntity == null) {
            ToastUtils.show(R.string.please_choose_classification);
            mShareButton.setEnabled(true);
            return;
        }
        if (!UserUtils.getsInstance().isLogin()) {
            LoginActivity.startActivity(getContext());
            ToastUtils.show(R.string.error_login);
            mShareButton.setEnabled(true);
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        // ????????????
        new Thread(() -> {
            LoadingPopupView loadingPopupView = new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true).asLoading(null, R.layout.view_loading);
            List<LocalMedia> data = mImageAdapter.getData();
            // ????????????????????????
            List<Map<String, Object>> imagesURL = new ArrayList<>();
            int errorCount = 0; // ????????????
            for (int i = 0; i < data.size(); i++) {
                String loadingTitle = getString(R.string.loading_hint_2, i, data.size());
                handler.post(() -> loadingPopupView.setTitle(loadingTitle).show());
                LocalMedia media = data.get(i);
                try {
                    Uri uri = media.isCut() ? Uri.fromFile(new File(media.getAvailablePath())) : Uri.parse(media.getAvailablePath());
                    String url = OSSUtils.uploadImage(uri, getString(R.string.image_format,
                            UserUtils.getsInstance().getUserInfo().getUserId(), new Date().getTime()));
                    Map<String, Object> map = new HashMap<>();
                    map.put("url", url);
                    if (media.isCut())
                        map.put("ratio", media.getCropResultAspectRatio());
                    else { // ????????????
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
                        map.put("ratio", bitmap.getWidth() * 1.0 / bitmap.getHeight());
                    }
                    Log.e(TAG, "shareClick: " + map);
                    imagesURL.add(map);
                } catch (ClientException | ServiceException | FileNotFoundException e) {
                    e.printStackTrace();
                    if (errorCount < 3) {
                        String errorTitle = String.format("????????????????????????????????? %d/%d", ++errorCount, 3);
                        handler.post(() -> ToastUtils.show(errorTitle));
                        i--;
                    } else {
                        handler.post(() -> {
                            loadingPopupView.setTitle("????????????????????????????????????").delayDismiss(1000);
                            mShareButton.setEnabled(true);
                        });
                        return;
                    }
                }
            }
            // ????????????
            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                    .publishImageText(title, mClassificationEntity.getClassificationId(), new Gson().toJson(imagesURL),
                            mComplexStyleRadioButton.isChecked() ? "complex" : "simple")
                    .subscribeOn(Schedulers.io()) // ?????????????????????
                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                    .subscribe(resp -> {   // ????????????
                        if (resp.getCode() == 0) { // ????????????
                            ToastUtils.show(resp.getData());
                            getActivity().finish();
                        } else {
                            throw new BaseException(resp.getMsg());
                        }
                        mShareButton.setEnabled(true);
                        loadingPopupView.setTitle(getString(R.string.successfully_upload)).delayDismiss(1000);
                    }, (RxjavaThrowable) throwable -> {   // ????????????
                        loadingPopupView.dismiss();
                        mShareButton.setEnabled(true);
                    });
            mDisposables.add(subscribe);
        }).start();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.classification_layout)
    public void onClassificationClick() {
        mClassificationResultCallBack = result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Serializable classification = result.getData().getSerializableExtra("classification");
                if (classification instanceof ClassificationEntity) { // ?????????????????????
                    mClassificationEntity = (ClassificationEntity) classification;
                    mClassificationTextView.setText(mClassificationEntity.getName());
                }
            }
        };
        ClassificationActivity.startActivityForResult(this, mClassificationResultLauncher);
    }

    /**
     * ???????????????
     */
    @Override
    public void onItemClick(View v, int position) {
        // ??????????????????????????????
        PictureSelector.create(this)
                .openPreview()
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectorUIStyle(selectorStyle)
                .isPreviewFullScreenMode(true)
                .startActivityPreview(position, true, mImageAdapter.getData());
    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public void openPicture() {
        // ????????????
        PictureSelector.create(getContext())
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(selectorStyle)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCropEngine(this) // ??????
                .setMaxSelectNum(6) // ????????????????????????
                .setSelectedData(mImageAdapter.getData())
                .forResult(new OnResultCallbackListener<LocalMedia>() { // ???????????????????????????
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        mImageAdapter.getData().clear();
                        mImageAdapter.notifyDataSetChanged();
                        mImageAdapter.getData().addAll(result);
                        mImageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

    /**
     * ??????????????????
     */
    @Override
    public void onStartCrop(Fragment fragment, LocalMedia currentLocalMedia, ArrayList<LocalMedia> dataSource, int requestCode) {
        // 1?????????????????????????????????
        String currentCropPath = currentLocalMedia.getAvailablePath();
        Uri inputUri;
        if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
            inputUri = Uri.parse(currentCropPath);
        } else {
            inputUri = Uri.fromFile(new File(currentCropPath));
        }
        String fileName = DateUtils.getCreateFileName("CROP_") + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(FileUtils.getFilePath(getContext()), fileName));
        ArrayList<String> dataCropSource = new ArrayList<>();
        for (int i = 0; i < dataSource.size(); i++) {
            LocalMedia media = dataSource.get(i);
            dataCropSource.add(media.getAvailablePath());
        }
        UCrop uCrop = UCrop.of(inputUri, destinationUri, dataCropSource);
        uCrop.setImageEngine(GlideEngine.createGlideEngine());
        uCrop.withOptions(buildUCropOptions(selectorStyle));
        uCrop.start(fragment.getActivity(), fragment, requestCode);
    }


    /**
     * ??????????????????????????????
     */
    private PictureSelectorStyle buildPictureSelectorOptions() {
        TitleBarStyle whiteTitleBarStyle = new TitleBarStyle();
        whiteTitleBarStyle.setTitleBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        whiteTitleBarStyle.setTitleDrawableRightResource(R.drawable.ic_primary_arrow_down);
        whiteTitleBarStyle.setTitleLeftBackResource(R.drawable.ps_ic_black_back);
        whiteTitleBarStyle.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
        whiteTitleBarStyle.setTitleCancelTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
        whiteTitleBarStyle.setDisplayTitleBarLine(true);

        BottomNavBarStyle whiteBottomNavBarStyle = new BottomNavBarStyle();
        whiteBottomNavBarStyle.setBottomNarBarBackgroundColor(Color.parseColor("#EEEEEE"));
        whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));

        whiteBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        whiteBottomNavBarStyle.setCompleteCountTips(false);
        whiteBottomNavBarStyle.setBottomEditorTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
        whiteBottomNavBarStyle.setBottomOriginalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));

        SelectMainStyle selectMainStyle = new SelectMainStyle();
        selectMainStyle.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        selectMainStyle.setDarkStatusBarBlack(true);
        selectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        selectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        selectMainStyle.setSelectBackground(R.drawable.ps_checkbox_selector);
        selectMainStyle.setSelectText(getString(R.string.ps_done_front_num));
        selectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));

        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        selectorStyle.setTitleBarStyle(whiteTitleBarStyle);
        selectorStyle.setBottomBarStyle(whiteBottomNavBarStyle);
        selectorStyle.setSelectMainStyle(selectMainStyle);

        return selectorStyle;
    }

    /**
     * ??????UCrop
     */
    private UCrop.Options buildUCropOptions(PictureSelectorStyle selectorStyle) {
        UCrop.Options options = new UCrop.Options();
        SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
        options.isDarkStatusBarBlack(mainStyle.isDarkStatusBarBlack());
        int statusBarColor = mainStyle.getStatusBarColor();
        options.setStatusBarColor(statusBarColor);
        options.setToolbarColor(statusBarColor);
        TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
        options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
        return options;
    }

}