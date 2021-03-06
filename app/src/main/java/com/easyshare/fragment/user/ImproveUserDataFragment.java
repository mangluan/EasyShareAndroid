package com.easyshare.fragment.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easyshare.R;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.base.ValueCallBack;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.FileUtils;
import com.easyshare.utils.OSSUtils;
import com.easyshare.utils.UserUtils;
import com.github.gzuliyujiang.dialog.DialogConfig;
import com.github.gzuliyujiang.dialog.DialogStyle;
import com.github.gzuliyujiang.wheelpicker.DatePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import org.greenrobot.eventbus.EventBus;

/**
 * ???????????????????????????????????????
 */
@SuppressLint("NonConstantResourceId")
public class ImproveUserDataFragment extends BaseFragment implements OnPermissionCallback {

    private ActivityResultLauncher<Intent> mPhotosActivityResultLauncher;
    private ActivityResultLauncher<Intent> mCameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> mCropImageActivityResultLauncher;

    private ImproveUserDataViewModel mViewModel;

    private Uri photoURI = null;

    @BindView(R.id.name_edit)
    public EditText mNameEditText;
    @BindView(R.id.avatar_image)
    public ImageView mAvatarImageView;
    @BindView(R.id.sex_btn)
    public Button mSexButton;
    @BindView(R.id.birthday_btn)
    public Button mBirthdayButton;

    public static ImproveUserDataFragment newInstance() {
        return new ImproveUserDataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_improve_user_data, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotosActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK)
                openCropImageActivity(CropImage.activity(result.getData().getData()));
        });
        mCameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK)
                openCropImageActivity(CropImage.activity(photoURI));
        });
        mCropImageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                uploadFiles(CropImage.getActivityResult(result.getData()).getUri());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ImproveUserDataViewModel.class);
        mViewModel.observeAvatarData(this, image -> {
            Glide.with(ImproveUserDataFragment.this).load(image)
                    .transition(withCrossFade()).into(mAvatarImageView);
            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                    .changeUserInfo(null, image, null, null, null, null)
                    .subscribeOn(Schedulers.io()) // ?????????????????????
                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                    .subscribe(resp -> {   // ????????????
                        RxjavaResponse.authentication(resp,getContext());
                        if (resp.getCode() == 0) {
                            ToastUtils.show(R.string.successfully_modify);
                        } else {
                            ToastUtils.show(resp.getMsg());
                        }
                    }, (RxjavaThrowable) throwable -> {   // ????????????
                    });
            mDisposables.add(subscribe);
        });
        mViewModel.observeSexData(this, sex -> mSexButton.setText(sex));
        mViewModel.observeBirthdayData(this, birthday -> {
            String[] split = birthday.split("-");
            mBirthdayButton.setText(getString(R.string.date_format_show, split[0], split[1], split[2]));
        });
    }

    /**
     * ????????????
     */
    @OnClick(R.id.skip_btn)
    public void onShipClick() {
//        new XPopup.Builder(getContext())
//                .asConfirm(null, "????????????????????????????????????????????????",
//                        "????????????", "?????????",
//                        (OnConfirmListener) () -> {
//                            int a = 10;
//                            int b = 20;
//                        }, null, false, 0)
//                .show();
        finish();
    }


    /**
     * ???????????? -> ????????????
     */
    @OnClick(R.id.avatar_image)
    public void onAvatarImageClick() {
        XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA)
                .permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .request(this);
    }

    /**
     * ?????????????????? ??? ???????????????????????????
     *
     * @param permissions ?????????
     * @param all         ??????????????????
     */
    @Override
    public void onGranted(List<String> permissions, boolean all) {
        if (all)  // ???????????????????????????
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .asCenterList(null, new String[]{getString(R.string.photos), getString(R.string.camera)},
                            (position, text) -> {
                                if (position == 0) {
                                    openPhotos();
                                } else {
                                    openCamera();
                                }
                            })
                    .show();
    }

    /**
     * ???????????????
     *
     * @param permissions ?????????
     * @param never       ?????????????????????
     */
    @Override
    public void onDenied(List<String> permissions, boolean never) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asConfirm(null, getString(R.string.permissions_hint), getString(R.string.talk_to_you_later), getString(R.string.to_agree),
                        () -> {
                            if (never) {   // ???????????? -> ???????????????????????????????????????
                                ToastUtils.show(R.string.permanent_denial_permission);
                                XXPermissions.startPermissionActivity(ImproveUserDataFragment.this, permissions);
                            } else {  // ????????????
                                onAvatarImageClick();
                            }
                        }, null, false, 0)
                .show();
    }


    /**
     * ????????????
     */
    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mPhotosActivityResultLauncher.launch(intent);
    }

    /**
     * ????????????
     */
    private void openCamera() {
        File imageFile = new File(FileUtils.getFilePath(getContext()), UUID.randomUUID().toString() + ".png");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //7.0?????????????????? Uri.fromFile(file)
            photoURI = Uri.fromFile(imageFile);
        } else {    // 7.0????????????????????????????????????
            photoURI = FileProvider.getUriForFile(getContext(), "com.easyshare.FileProvider", imageFile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //??????????????????????????????uri??????
        mCameraActivityResultLauncher.launch(intent);
    }

    /**
     * ???????????? 1???1
     */
    private void openCropImageActivity(CropImage.ActivityBuilder activityBuilder) {
        Intent intent = activityBuilder.setAspectRatio(1, 1)
                .setActivityMenuIconColor(getResources().getColor(R.color.black_translucence_eighty))
                .getIntent(getContext());
        mCropImageActivityResultLauncher.launch(intent);
    }

    /**
     * ????????????
     */
    private void uploadFiles(Uri uri) {
        LoadingPopupView loadingPopupView = new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true).asLoading(null, R.layout.view_loading);
        OSSUtils.addProgressCallBack(progress -> loadingPopupView.setTitle(getString(R.string.loading_hint, progress)).show());
        OSSUtils.uploadImage(uri, getString(R.string.avatar_format,UserUtils.getsInstance().getUserInfo().getUserId() , new Date().getTime() ),
                new ValueCallBack<String>() {

                    @Override
                    public void onSuccess(String image) {
                        mViewModel.setAvatarData(image);
                        loadingPopupView.setTitle(getString(R.string.successfully_upload)).delayDismiss(200);
                    }

                    @Override
                    public void onFail(String code) {
                        loadingPopupView.dismiss();
                        ToastUtils.show(code);
                    }
                });
    }

    /**
     * ????????????
     */
    @OnClick(R.id.sex_btn)
    public void onSexClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .isDestroyOnDismiss(true)
                .asCenterList(getString(R.string.set_sex), new String[]{getString(R.string.sex_man), getString(R.string.sex_woman)},
                        (position, text) -> mViewModel.setSexData(text))
                .show();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.birthday_btn)
    public void onBirthdayClick() {
        DialogConfig.setDialogStyle(DialogStyle.Three);
        DatePicker picker = new DatePicker(getActivity());
        picker.setTitle(getString(R.string.set_birthday));
        picker.getTitleView().setTextSize(16f);
        picker.getTitleView().setTextColor(getContext().getColor(R.color.black_translucence_eighty));
        picker.setBodyWidth(240);
        DateWheelLayout wheelLayout = picker.getWheelLayout();
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
        wheelLayout.setRange(DateEntity.target(1970, 1, 1), DateEntity.today(),
                DateEntity.target(2000, 1, 1));
        wheelLayout.setCyclicEnabled(true);  // ??????????????????
        wheelLayout.setTextColor(getResources().getColor(R.color.black_translucence_thirty));
        wheelLayout.setSelectedTextColor(getResources().getColor(R.color.black_translucence_eighty));
        picker.setOnDatePickedListener((year, month, day) -> mViewModel.setBirthdayData(getString(R.string.date_format_save, year, month, day)));
        picker.show();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.confirm_btn)
    public void onConfirmClick() {
        String nameData = mNameEditText.getText().toString().trim();
        String sexData = mViewModel.getSexData();
        String birthdayData = mViewModel.getBirthdayData();
        if (TextUtils.isEmpty(nameData) && TextUtils.isEmpty(sexData) && TextUtils.isEmpty(birthdayData)) {
            ToastUtils.show(getString(R.string.error_set_content));
        }
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .changeUserInfo(nameData, null, sexData, birthdayData, null, null)
                .subscribeOn(Schedulers.io()) // ?????????????????????
                .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                .subscribe(resp -> {   // ????????????
                    RxjavaResponse.authentication(resp,getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(R.string.successfully_modify);
                        finish();
                    } else {
                        ToastUtils.show(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // ????????????
                });
        mDisposables.add(subscribe);
    }

    /**
     * ????????????
     */
    private void finish() {
        EventBus.getDefault().post(BaseDataBean.build(Constants.OPEN_SELECT_CLASSIFICATION_ACTIVITY));
        getActivity().finish();
    }

}