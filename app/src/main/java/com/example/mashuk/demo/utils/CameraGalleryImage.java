package com.example.mashuk.demo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.FileProvider.getUriForFile;


/**
 * Created by jyubin on seek3/3/17.
 */

public class CameraGalleryImage {


    public static Uri imageURI = null;
    private static String mCurrentPhotoPath;


    public static void getCameraImage(Activity mContext, int cameraCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(mContext, Manifest.permission.CAMERA)) {
                requestPermission(mContext, Manifest.permission.CAMERA, cameraCode);
            } else if (!checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, cameraCode);
            } else {
                captureImageFromCamera(mContext, cameraCode);
            }
        } else {
            captureImageFromCamera(mContext, cameraCode);
        }
    }

    public static void getCameraImage(Fragment mContext, int cameraCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(mContext, Manifest.permission.CAMERA)) {
                requestPermission(mContext, Manifest.permission.CAMERA, cameraCode);
            } else if (!checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, cameraCode);
            } else {
                captureImageFromCamera(mContext, cameraCode);
            }
        } else {
            captureImageFromCamera(mContext, cameraCode);
        }
    }

    private static void captureImageFromCamera(Fragment fragment, int cameraCode) {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mContext.startActivityForResult(intent, cameraCode);*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
//                photoFile = createImageFile(fragment.getActivity(), "jpg", Environment.DIRECTORY_PICTURES);
                photoFile = createImageFile("jpg", fragment.getActivity());

            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getUriForFile(fragment.getActivity(), fragment.getActivity().getPackageName() + ".provider",
                        photoFile);
//                Uri photoURI = Uri.fromFile(photoFile);
                imageURI = photoURI;

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                fragment.startActivityForResult(intent, cameraCode);
            }
        }
    }

    public static String getImagePath() {
        return mCurrentPhotoPath;
    }


    public static File createImageFile(String extension, Context context) throws IOException {
        // Create an image file name
        String imageFileName = extension + "_" + (System.currentTimeMillis());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + "." + extension);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private static void captureImageFromCamera(Activity mContext, int cameraCode) {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mContext.startActivityForResult(intent, cameraCode);*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
//                photoFile = createImageFile(fragment.getActivity(), "jpg", Environment.DIRECTORY_PICTURES);
                photoFile = createImageFile("jpg", mContext);

            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getUriForFile(mContext.getApplicationContext(), mContext.getPackageName() + ".provider", photoFile);
//                Uri photoURI = Uri.fromFile(photoFile);
                imageURI = photoURI;

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mContext.startActivityForResult(intent, cameraCode);
            }
        }
    }

    public static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, Activity signupActivity) {
        Cursor cursor = signupActivity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public static void requestPermission(Activity mContext, String camera, int cameraCode) {
        if (ActivityCompat.checkSelfPermission(mContext, camera)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{camera},
                    cameraCode);
        }
    }

    public static void requestPermission(Fragment mContext, String camera, int cameraCode) {
        if (ActivityCompat.checkSelfPermission(mContext.getContext(), camera)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext.getActivity(),
                    new String[]{camera},
                    cameraCode);
        }
    }

    public static boolean checkPermission(Activity mContext, String per) {
        int result = ContextCompat.checkSelfPermission(mContext, per);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermission(Fragment mContext, String per) {
        int result = ContextCompat.checkSelfPermission(mContext.getContext(), per);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public static void getGalleryImage(Activity mContext, int cameraCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, cameraCode);
            } else {
                gelleryCall(mContext, cameraCode);
            }
        } else {
            gelleryCall(mContext, cameraCode);
        }
    }

    public static void getGalleryImage(Fragment mContext, int cameraCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, cameraCode);
            } else {

                gelleryCall(mContext, cameraCode);

            }
        } else {

            gelleryCall(mContext, cameraCode);

        }
    }

    public void openGallery(Fragment mContext, int limit) {

        /*Intent intent = new Intent(mContext, Gallery.class);
        intent.putExtra("title", "Select media");
        intent.putExtra("mode", 1);
        intent.putExtra("maxSelection",limit );
        mContext.startActivityForResult(intent, Constant.OPEN_MEDIA_PICKER);*/
    }

    public static void getGalleryImageMultiple(Activity mContext, int cameraCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, cameraCode);
            } else {
                gelleryCall(mContext, cameraCode);
            }
        } else {
            gelleryCall(mContext, cameraCode);
        }
    }

    private static void gelleryCall(Activity mContext, int cameraCode) {
        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mContext.startActivityForResult(pictureActionIntent, cameraCode);
    }

    private static void gelleryCall(Fragment mContext, int cameraCode) {
        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mContext.startActivityForResult(pictureActionIntent, cameraCode);
    }

    private static void gelleryCallMultiImage(Activity mContext, int cameraCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mContext.startActivityForResult(Intent.createChooser(intent, "Select Picture"), cameraCode);
    }

    private static void gelleryCallMultiImage(Fragment mContext, int cameraCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mContext.startActivityForResult(Intent.createChooser(intent, "Select Picture"), cameraCode);

    }

    public static Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

    public static Uri retunPhotoURI() {
        return imageURI;
    }
}
