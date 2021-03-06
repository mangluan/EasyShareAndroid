package com.easyshare.fragment.user;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.easyshare.entity.UserInfoEntity;
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
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.theartofdev.edmodo.cropper.CropImage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ????????????
 */
@SuppressLint("NonConstantResourceId")
public class PersonalInformationFragment extends BaseFragment implements OnPermissionCallback {

    private PersonalInformationViewModel mViewModel;

    private ActivityResultLauncher<Intent> mPhotosActivityResultLauncher;
    private ActivityResultLauncher<Intent> mCameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> mCropImageActivityResultLauncher;

    private Uri photoURI = null;

    @BindView(R.id.avatarImage)
    ImageView mAvatarImage;
    @BindView(R.id.user_name)
    TextView mUserNameTextView;
    @BindView(R.id.sign)
    TextView mSignTextView;
    @BindView(R.id.sex)
    TextView mSexTextView;
    @BindView(R.id.birthday)
    TextView mBirthdayTextView;


    public static PersonalInformationFragment newInstance() {
        return new PersonalInformationFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_information, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PersonalInformationViewModel.class);
        initUserInfo();
    }

    /**
     * ?????????????????????
     */
    private void initUserInfo() {
        if (UserUtils.getsInstance().isLogin()) {
            UserInfoEntity userInfo = UserUtils.getsInstance().getUserInfo();
            Glide.with(this).load(userInfo.getAvatarImage())
                    .transition(withCrossFade()).into(mAvatarImage);
            mUserNameTextView.setText(userInfo.getName());
            mSignTextView.setText(TextUtils.isEmpty(userInfo.getSign()) ? getText(R.string.text_null) : userInfo.getSign());
            mSexTextView.setText(TextUtils.isEmpty(userInfo.getSex()) ? getText(R.string.text_null) : userInfo.getSex());
            mBirthdayTextView.setText(TextUtils.isEmpty(userInfo.getBirthday()) ? getText(R.string.text_null) : userInfo.getBirthday());
        } else { // ??????????????????????????????????????????????????????
            ToastUtils.show(R.string.error_login);
        }
    }

    /**
     * ????????????
     */
    @OnClick(R.id.avatar_layout)
    public void onAvatarLayoutClick() {
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
                    .asCenterList(getString(R.string.update_avatar),
                            new String[]{getString(R.string.photos), getString(R.string.camera)},
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
                .asConfirm(null, getString(R.string.permissions_hint),
                        getString(R.string.talk_to_you_later), getString(R.string.to_agree),
                        () -> {
                            if (never) {   // ???????????? -> ???????????????????????????????????????
                                ToastUtils.show(R.string.permanent_denial_permission);
                                XXPermissions.startPermissionActivity(this, permissions);
                            } else {  // ????????????
                                onAvatarLayoutClick();
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
        OSSUtils.uploadImage(uri, getString(R.string.avatar_format, UserUtils.getsInstance().getUserInfo().getUserId(), new Date().getTime()),
                new ValueCallBack<String>() {

                    @Override
                    public void onSuccess(String image) {
                        updateAvatarInfo(image);
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
     * ??????????????????
     */
    private void updateAvatarInfo(String url) {
        Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                .changeUserInfo(null, url, null, null, null, null)
                .subscribeOn(Schedulers.io()) // ?????????????????????
                .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                .subscribe(resp -> {   // ????????????
                    RxjavaResponse.authentication(resp, getContext());
                    if (resp.getCode() == 0) {
                        ToastUtils.show(R.string.successfully_modify);
                        Glide.with(this).load(url)
                                .transition(withCrossFade()).into(mAvatarImage);
                        // ????????????????????????????????????
                        UserUtils.getsInstance().getUserInfo().setAvatarImage(url);
                        sendModificationSuccessNotification();
                    } else {
                        ToastUtils.show(resp.getMsg());
                    }
                }, (RxjavaThrowable) throwable -> {   // ????????????
                });
        mDisposables.add(subscribe);
    }

    /**
     * ???????????????
     */
    @OnClick(R.id.user_name_layout)
    public void onUserNameLayoutClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onCreated(BasePopupView popupView) {
                        super.onCreated(popupView);
                        if (popupView instanceof InputConfirmPopupView) {
                            EditText editText = ((InputConfirmPopupView) popupView).getEditText();
                            editText.setText(UserUtils.getsInstance().getUserInfo().getName());
                            editText.setSelection(0, editText.getText().length());
                        }
                    }
                })
                .asInputConfirm(null, getString(R.string.update_user_name), getString(R.string.user_name_hint),
                        userName -> {
                            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                                    .changeUserInfo(userName, null, null, null, null, null)
                                    .subscribeOn(Schedulers.io()) // ?????????????????????
                                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                                    .subscribe(resp -> {   // ????????????
                                        RxjavaResponse.authentication(resp, getContext());
                                        if (resp.getCode() == 0) {
                                            ToastUtils.show(R.string.successfully_modify);
                                            mUserNameTextView.setText(userName);
                                            // ????????????????????????????????????
                                            UserUtils.getsInstance().getUserInfo().setName(userName);
                                            sendModificationSuccessNotification();
                                        } else {
                                            ToastUtils.show(resp.getMsg());
                                        }
                                    }, (RxjavaThrowable) throwable -> {   // ????????????
                                    });
                            mDisposables.add(subscribe);
                        })
                .show();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.sign_layout)
    public void onSignLayoutClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onCreated(BasePopupView popupView) {
                        super.onCreated(popupView);
                        if (popupView instanceof InputConfirmPopupView &&
                                !TextUtils.isEmpty(UserUtils.getsInstance().getUserInfo().getSign())) {
                            EditText editText = ((InputConfirmPopupView) popupView).getEditText();
                            editText.setText(UserUtils.getsInstance().getUserInfo().getSign());
                            editText.setSelection(0, editText.getText().length());
                            editText.setMaxLines(20);
                        }
                    }
                })
                .asInputConfirm(null, TextUtils.isEmpty(UserUtils.getsInstance().getUserInfo().getSign())
                                ? getString(R.string.set_sign) : getString(R.string.update_sign),
                        getString(R.string.sign_hint), sign -> {
                            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                                    .changeUserInfo(null, null, null, null, sign, null)
                                    .subscribeOn(Schedulers.io()) // ?????????????????????
                                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                                    .subscribe(resp -> {   // ????????????
                                        RxjavaResponse.authentication(resp, getContext());
                                        if (resp.getCode() == 0) {
                                            ToastUtils.show(R.string.successfully_modify);
                                            mSignTextView.setText(sign);
                                            // ????????????????????????????????????
                                            UserUtils.getsInstance().getUserInfo().setSign(sign);
                                            sendModificationSuccessNotification();
                                        } else {
                                            ToastUtils.show(resp.getMsg());
                                        }
                                    }, (RxjavaThrowable) throwable -> {   // ????????????
                                    });
                            mDisposables.add(subscribe);
                        })
                .show();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.sex_layout)
    public void onSexLayoutClick() {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .asCenterList(TextUtils.isEmpty(UserUtils.getsInstance().getUserInfo().getSex())
                                ? getString(R.string.set_sex) : getString(R.string.update_sex),
                        new String[]{getString(R.string.sex_man), getString(R.string.sex_woman)},
                        (position, sex) -> {
                            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                                    .changeUserInfo(null, null, sex, null, null, null)
                                    .subscribeOn(Schedulers.io()) // ?????????????????????
                                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                                    .subscribe(resp -> {   // ????????????
                                        RxjavaResponse.authentication(resp, getContext());
                                        if (resp.getCode() == 0) {
                                            ToastUtils.show(R.string.successfully_modify);
                                            mSexTextView.setText(sex);
                                            // ????????????????????????????????????
                                            UserUtils.getsInstance().getUserInfo().setSex(sex);
                                            sendModificationSuccessNotification();
                                        } else {
                                            ToastUtils.show(resp.getMsg());
                                        }
                                    }, (RxjavaThrowable) throwable -> {   // ????????????
                                    });
                            mDisposables.add(subscribe);
                        })
                .show();
    }

    /**
     * ????????????
     */
    @OnClick(R.id.birthday_layout)
    public void onBirthdayLayoutClick() {
        DialogConfig.setDialogStyle(DialogStyle.Three);
        DatePicker picker = new DatePicker(getActivity());
        picker.setTitle(TextUtils.isEmpty(UserUtils.getsInstance().getUserInfo().getSex()) ?
                getString(R.string.set_birthday) : getString(R.string.update_birthday));
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
        picker.setOnDatePickedListener((year, month, day) -> {
            Disposable subscribe = RetrofitFactory.getsInstance(getContext())
                    .changeUserInfo(null, null, null,
                            getString(R.string.date_format_save, year, month, day), null, null)
                    .subscribeOn(Schedulers.io()) // ?????????????????????
                    .observeOn(AndroidSchedulers.mainThread()) // ???????????????
                    .subscribe(resp -> {   // ????????????
                        RxjavaResponse.authentication(resp, getContext());
                        if (resp.getCode() == 0) {
                            ToastUtils.show(R.string.successfully_modify);
                            String birthday = getString(R.string.date_format_show,
                                    String.valueOf(year), String.valueOf(month), String.valueOf(day));
                            mBirthdayTextView.setText(birthday);
                            // ????????????????????????????????????
                            UserUtils.getsInstance().getUserInfo().setBirthday(birthday);
                            sendModificationSuccessNotification();
                        } else {
                            ToastUtils.show(resp.getMsg());
                        }
                    }, (RxjavaThrowable) throwable -> {   // ????????????
                    });
            mDisposables.add(subscribe);
        });
        picker.show();
    }

    /**
     * ????????????????????????
     */
    private void sendModificationSuccessNotification() {
        EventBus.getDefault().post(BaseDataBean.build(Constants.UPDATE_USER_INFORMATION,
                UserUtils.getsInstance().getUserInfo()));
    }

}