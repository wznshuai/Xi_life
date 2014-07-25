package com.nostra13.universalimageloader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageShowInBGAware extends ImageViewAware {

	public ImageShowInBGAware(ImageView imageView){
		super(imageView);
	}
	
	public ImageShowInBGAware(ImageView imageView, boolean checkActualViewSize) {
		super(imageView, checkActualViewSize);
	}

	public boolean setImageDrawableInBackground(Drawable drawable) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setBackgroundDrawable(drawable);
			return true;
		}
		return false;
	}

	public boolean setImageBitmapInBackground(Bitmap bitmap) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			return true;
		}
		return false;
	}
}
