package com.example.hw1125_cplist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	//ContentResolver
	ContentResolver resolverAudio;
	ContentResolver resolverImage;
	ContentResolver resolverPhone;

	//端末SE
	private static final String[] projectionAudio = new String[] {
			MediaStore.Audio.AudioColumns.TITLE,
			MediaStore.Audio.AudioColumns.DATA
	};

	//写真 IDのみ
	private static final String[] projectionImage = new String[] {
			MediaStore.Images.ImageColumns._ID
	};

	//電話帳 名前と番号
	private static final String[] projectionPhone = new String[] {
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.cplistView);

		List<ArticleDTO> relatedDto = new ArrayList<ArticleDTO>();

		//コンテントにアクセスする領域を取得
		resolverAudio = getContentResolver();
		resolverImage = getContentResolver();
		resolverPhone = getContentResolver();

		//queryはSQLを動かす仕組み
		//戻り値はCursor、データの場所を指す
		Cursor cursorAudio = resolverAudio.query(
				//端末SE
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
				projectionAudio, null, null, null);

		Cursor cursorImage = resolverImage.query(
				//写真
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projectionImage, null, null, null);

		Cursor cursorPhone = resolverPhone.query(
				//電話帳
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projectionPhone, null, null, null);

		Log.i("ContentProvider", "[cursorAudio.getCount] " + cursorAudio.getCount());
		Log.i("ContentProvider", "[cursorImage.getCount] " + cursorImage.getCount());
		Log.i("ContentProvider", "[cursorPhone.getCount] " + cursorPhone.getCount());

		/*--------------------------------------------------------------------*/
		//端末内の画像のURIを取得してリストにつなぐ
		/*--------------------------------------------------------------------*/
		List<Uri> pictureList = new ArrayList<Uri>();

		if (cursorImage != null && 0 < cursorImage.getCount()) {
			//cursorがおかしなところを指しているかもなので先頭に移動
			cursorImage.moveToFirst();

			//写真のidのIndex取得
			int fileIndex = cursorImage.getColumnIndex(MediaStore.Images.ImageColumns._ID);

			do {
				//写真データのidを取得する
				Long id = cursorImage.getLong(fileIndex);

				//idをURIに変換
				Uri bmpUri = ContentUris.withAppendedId(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
				//TODO 今回は画像のURIだけリストに入れる
				pictureList.add(bmpUri);

				//TODO デバッグ用表示 端末写真の情報
//				Log.i("ContentProvider", "id " + id);

				//カーソルを一個動かす、次がないとFalse
			} while (cursorImage.moveToNext());

		}

		/*--------------------------------------------------------------------*/
		//端末内のSEのURIを取得してリストにつなぐ
		/*--------------------------------------------------------------------*/
		//TODO 端末SEの扱いは今回は見送り
//		List<String> playList = new ArrayList<String>();
//
//		if (cursorAudio != null && 0 < cursorAudio.getCount()) {
//			//cursorがおかしなところを指しているかもなので先頭に移動
//			cursorAudio.moveToFirst();
//
//			//端末SE
////			int titleIndex = cursorAudio.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
//			int dataIndex = cursorAudio.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
//
//			do {
//				//データの文字列を取得する
//				//端末SE
////				String title = cursorAudio.getString(titleIndex);
//				String data = cursorAudio.getString(dataIndex);
//
//				//TODO 今回はSEのURIだけリストに入れる
//				playList.add(data);
//
//				//TODO デバッグ用表示 端末SE
////				Log.i("ContentProvider", "title " + title);
////				Log.i("ContentProvider", "data " + data);
//
//				//カーソルを一個動かす、次がないとFalse
//			} while (cursorAudio.moveToNext());
//
//		}

		/*--------------------------------------------------------------------*/
		//電話帳、名前と番号を取得してDTOに入れる
		//画像のURIはランダムに入れ込む
		/*--------------------------------------------------------------------*/
		if (cursorPhone != null && 0 < cursorPhone.getCount()) {
			//cursorがおかしなところを指しているかもなので先頭に移動
			cursorPhone.moveToFirst();

			//電話帳
			int nameIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int numberIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

			do {
				ArticleDTO dto = new ArticleDTO();

				//データの文字列を取得する
				//電話帳
				String name = cursorPhone.getString(nameIndex);
				String number = cursorPhone.getString(numberIndex);

				//画像はランダムに登録
				dto.setPictureImageURI(
						pictureList.get(new Random().nextInt(pictureList.size()))
						);
				dto.setNameTxt(name);
				dto.setTelNumberTxt(number);
				//SEはランダムに登録
				//TODO 今回SEは見送り
//				dto.setPlaySEURI(
//						playList.get(new Random().nextInt(playList.size()))
//						);

				//リストにつなぐ
				relatedDto.add(dto);

				//TODO デバッグ用表示 電話帳
//				Log.i("ContentProvider", "name " + name);
//				Log.i("ContentProvider", "number " + number);

				//カーソルを一個動かす、次がないとFalse
			} while (cursorPhone.moveToNext());

		}

		//ArticleDTO型のカスタムアダプターを使用する
		CustomAdapter adapter = new CustomAdapter(
				this,
				R.layout.item_list,
				relatedDto);
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class CustomAdapter extends ArrayAdapter<ArticleDTO> {
		private int resource;

		public CustomAdapter(Context context, int resource, List<ArticleDTO> objects) {
			super(context, resource, objects);
			// TODO 自動生成されたコンストラクター・スタブ
			//リソースファイルは後で使うので、メンバ変数に保持しておく。
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO 自動生成されたメソッド・スタブ
			/* レイアウトを生成する */
			convertView = View.inflate(getContext(), this.resource, null);

			/* 生成したViewから各パーツを取得する */
			ImageView pictureImage = (ImageView) convertView.findViewById(R.id.pictureImage);
			TextView nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			TextView telNumberTxt = (TextView) convertView.findViewById(R.id.telNumberTxt);
			//TODO 内部SE鳴らすのは今回は見送り
//			ImageView playImage = (ImageView) convertView.findViewById(R.id.playImage);

			ArticleDTO item = getItem(position);

			Uri pictureImageURI = item.getPictureImageURI();

			try {
				//Bitmapを軽いクオリティに変化する
				Bitmap bitmap = getBitmap(pictureImageURI);
				//TODO こっちを使いたいところだがoutofmemory。。。
//				Bitmap bitmap = MediaStore.Images.Media
//						.getBitmap(getContentResolver(), pictureImageURI);
				pictureImage.setImageBitmap(bitmap);
			} catch (IOException e) {
				// TODO: handle exception
				Log.e("error", "getView IOException");
			}

			nameTxt.setText(item.getNameTxt());
			telNumberTxt.setText(item.getTelNumberTxt());

			return convertView;
		}

	}

	/*------------------------------------------------------------------------*/
	//BitMap読み込みメソッド
	//クオリティを調整できる
	/*------------------------------------------------------------------------*/
	public Bitmap getBitmap(Uri imageUri) throws IOException {
		BitmapFactory.Options mOptions = new BitmapFactory.Options();
		mOptions.inSampleSize = 10;
		Bitmap resizeBitmap = null;

		InputStream is = resolverImage.openInputStream(imageUri);
		resizeBitmap = BitmapFactory.decodeStream(is, null, mOptions);
		is.close();

		return resizeBitmap;
	}

}
