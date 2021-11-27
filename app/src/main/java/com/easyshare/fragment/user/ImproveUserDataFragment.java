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
import com.easyshare.base.BaseFragment;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.base.RxjavaThrowable;
import com.easyshare.base.ValueCallBack;
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

/**
 * 注册后初始化基本用户信息页
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
                    .subscribeOn(Schedulers.io()) // 子线程执行方法
                    .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                    .subscribe(resp -> {   // 成功回调
                        RxjavaResponse.authentication(resp,getContext());
                        if (resp.getCode() == 0) {
                            ToastUtils.show(R.string.successfully_modify);
                        } else {
                            ToastUtils.show(resp.getMsg());
                        }
                    }, (RxjavaThrowable) throwable -> {   // 出错回调
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
     * 跳过点击
     */
    @OnClick(R.id.skip_btn)
    public void onShipClick() {
//        new XPopup.Builder(getContext())
//                .asConfirm(null, "您的帐号未设置密码！请先设置密码",
//                        "以后再说", "去设置",
//                        (OnConfirmListener) () -> {
//                            int a = 10;
//                            int b = 20;
//                        }, null, false, 0)
//                .show();
        getActivity().finish();
    }


    /**
     * 头像点击 -> 判断权限
     */
    @OnClick(R.id.avatar_image)
    public void onAvatarImageClick() {
        XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA)
                .permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .request(this);
    }

    /**
     * 权限判断通过 ： 全部通过才处理逻辑
     *
     * @param permissions 权限名
     * @param all         是否全部通过
     */
    @Override
    public void onGranted(List<String> permissions, boolean all) {
        if (all)  // 打开相机相册选择框
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
     * 权限被拒绝
     *
     * @param permissions 权限名
     * @param never       是否永远被拒绝
     */
    @Override
    public void onDenied(List<String> permissions, boolean never) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asConfirm(null, getString(R.string.permissions_hint), getString(R.string.talk_to_you_later), getString(R.string.to_agree),
                        () -> {
                            if (never) {   // 永久拒绝 -> 跳转到应用权限系统设置页面
                                ToastUtils.show(R.string.permanent_denial_permission);
                                XXPermissions.startPermissionActivity(ImproveUserDataFragment.this, permissions);
                            } else {  // 普通拒绝
                                onAvatarImageClick();
                            }
                        }, null, false, 0)
                .show();
    }


    /**
     * 打开相机
     */
    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mPhotosActivityResultLauncher.launch(intent);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        File imageFile = new File(FileUtils.getFilePath(getContext()), UUID.randomUUID().toString() + ".png");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //7.0以下的系统用 Uri.fromFile(file)
            photoURI = Uri.fromFile(imageFile);
        } else {    // 7.0以上的系统用下面这种方案
            photoURI = FileProvider.getUriForFile(getContext(), "com.easyshare.FileProvider", imageFile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //将图片文件转化为一个uri传入
        mCameraActivityResultLauncher.launch(intent);
    }

    /**
     * 打开剪裁 1：1
     */
    private void openCropImageActivity(CropImage.ActivityBuilder activityBuilder) {
        Intent intent = activityBuilder.setAspectRatio(1, 1)
                .setActivityMenuIconColor(getResources().getColor(R.color.black_translucence_eighty))
                .getIntent(getContext());
        mCropImageActivityResultLauncher.launch(intent);
    }

    /**
     * 上传文件
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
     * 性别点击
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
     * 生日点击
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
        wheelLayout.setCyclicEnabled(true);  // 开启循环滚动
        wheelLayout.setTextColor(getResources().getColor(R.color.black_translucence_thirty));
        wheelLayout.setSelectedTextColor(getResources().getColor(R.color.black_translucence_eighty));
        picker.setOnDatePickedListener((year, month, day) -> mViewModel.setBirthdayData(getString(R.string.date_format_save, year, month, day)));
        picker.show();
    }

    /**
     * 提交按钮
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
                .subscribeOn(Schedulers.io()) // 子线程执行方法
                .observeOn(AndroidSchedulers.mainThread()) // 主线程回调
                .subscribe(resp -> {   // 成功回调
                    RxjavaResponse.authentication(resp,getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(R.string.successfully_modify);
                        getActivity().finish();
                    } else {
                        ToastUtils.show(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // 出错回调
                });
        mDisposables.add(subscribe);
    }

}