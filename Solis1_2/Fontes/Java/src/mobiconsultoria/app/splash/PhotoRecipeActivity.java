package mobiconsultoria.app.splash;

import java.io.File;

import mobiconsultoria.app.constantes.Constantes;
import mobiconsultoria.app.utils.DetectaConexao;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class PhotoRecipeActivity extends Activity {

	private DetectaConexao detectaConexao = null;
	private WebView wvSolis = null;

	private static final int FILECHOOSER_RESULTCODE = 2888;
	private ValueCallback<Uri> mUploadMessage;
	private Uri mCapturedImageURI = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo_recipe);

		wvSolis = (WebView) findViewById(R.id.wvSolis);

		detectaConexao = new DetectaConexao(getApplicationContext());

		if (!detectaConexao.existeConexao()) {
			Toast.makeText(getApplicationContext(), getString(R.string.msg_network_status), Toast.LENGTH_LONG).show();

			finish();

			return;
		}

		wvSolis.getSettings().setJavaScriptEnabled(true);

		wvSolis.getSettings().setLoadWithOverviewMode(true);

		wvSolis.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		wvSolis.setScrollbarFadingEnabled(false);
		wvSolis.getSettings().setBuiltInZoomControls(true);
		wvSolis.getSettings().setPluginState(PluginState.ON);
		wvSolis.getSettings().setAllowFileAccess(true);
		wvSolis.getSettings().setSupportZoom(true);

		wvSolis.loadUrl(Constantes.URLSOLIS);

		startWebView();
	}

	private void startWebView() {

		wvSolis.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			// If you will not use this method url links are open in new brower
			// not in webview
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// Check if Url contains ExternalLinks string in url
				// then open url in new browser
				// else all webview links will open in webview browser
				if (url.contains("google")) {

					// Could be cleverer and use a regex
					// Open links in new browser
					view.getContext().startActivity(
							new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

					// Here we can open new activity

					return true;

				} else {

					// Stay within this webview and load url
					view.loadUrl(url);
					return true;
				}

			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {

				// if url contains string androidexample
				// Then show progress Dialog
				if (progressDialog == null && url.contains("solisfarma")) {

					// in standard case YourActivity.this
					progressDialog = new ProgressDialog(
							PhotoRecipeActivity.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			// Called when all page resources loaded
			public void onPageFinished(WebView view, String url) {

				try {
					// Close progressDialog
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		});

		// You can create external class extends with WebChromeClient
		// Taking WebViewClient as inner class
		// we will define openFileChooser for select file from camera or sdcard

		wvSolis.setWebChromeClient(new WebChromeClient() {

			// openFileChooser for Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {

				// Update message
				mUploadMessage = uploadMsg;

				try {

					// Create AndroidExampleFolder at sdcard

					File imageStorageDir = new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
							"PhotoRecipeFolder");

					if (!imageStorageDir.exists()) {
						// Create AndroidExampleFolder at sdcard
						imageStorageDir.mkdirs();
					}

					// Create camera captured image file path and name
					File file = new File(imageStorageDir + File.separator
							+ "IMG_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg");

					mCapturedImageURI = Uri.fromFile(file);

					// Camera capture image intent
					final Intent captureIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

					captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							mCapturedImageURI);

					Intent i = new Intent(Intent.ACTION_GET_CONTENT);
					i.addCategory(Intent.CATEGORY_OPENABLE);
					i.setType("image/*");

					// Create file chooser intent
					Intent chooserIntent = Intent.createChooser(i,
							getString(R.string.msg_title_information));

					// Set camera intent to file chooser
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
							new Parcelable[] { captureIntent });

					// On select image call onActivityResult method of activity
					startActivityForResult(chooserIntent,
							FILECHOOSER_RESULTCODE);

				} catch (Exception e) {
					Toast.makeText(getBaseContext(), getString(R.string.msg_exception_status) + " - Exceção ocorrida: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}

			// openFileChooser for Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

			// openFileChooser for other Android versions
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {

				openFileChooser(uploadMsg, acceptType);
			}

			// The webPage has 2 filechoosers and will send a
			// console message informing what action to perform,
			// taking a photo or updating the file

			public boolean onConsoleMessage(ConsoleMessage cm) {

				onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
				return true;
			}

			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				Log.d("androidruntime",
				"Show console messages, Used for debugging: " + message);

			}
		}); // End setWebChromeClient

	}

	// Return here when file selected from camera or from SDcard

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (requestCode == FILECHOOSER_RESULTCODE) {

			if (null == this.mUploadMessage) {
				return;

			}

			Uri result = null;

			try {
				if (resultCode != RESULT_OK) {

					result = null;

				} else {

					// retrieve from the private variable if the intent is null
					result = intent == null ? mCapturedImageURI : intent
							.getData();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "activity :" + e,
						Toast.LENGTH_LONG).show();
			}

			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

		}

	}

	// Open previous opened link from history on webview when back button
	// pressed

	@Override
	// Detect when the back button is pressed
	public void onBackPressed() {

		if (wvSolis.canGoBack()) {

			wvSolis.goBack();

		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

}
