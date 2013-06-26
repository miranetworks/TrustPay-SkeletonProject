package com.example.skeletonapp;


import java.util.Date;

import com.example.skeletonapp.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import biz.trustpay.api.BillingResult;
import biz.trustpay.api.PricePointListener;
import biz.trustpay.api.PricePoints;
import biz.trustpay.api.TrustPayApi;


public class MainActivity extends Activity implements PricePointListener
{
	private static final String CONST_APP_ID = "ap.317cf227-c4e2-44c1-a333-bd431d62b371";
	String appuser = "appuser";
	String txDescription = "Skeleton App";

	TrustPayApi fTrustPayApi;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		setUpMainForm();

		fTrustPayApi = new TrustPayApi(this, CONST_APP_ID);
		fTrustPayApi.setPricePointListener(this);       
    }

	// -------------------------------------------------------------------

	@Override
	protected void onStart() {
		super.onStart();
		fTrustPayApi.doBindService(true);
	}

	// -------------------------------------------------------------------

	@Override
	protected void onStop() {
		super.onStop();
		fTrustPayApi.doUnbindService();
	}


	private void setUpMainForm() {
		// TODO Auto-generated method stub
		Button pay = (Button) findViewById(R.id.btnPay);
		pay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText et = (EditText) findViewById(R.id.etResult);
				et.setText("");

				String amountStr = "5";
				String currencyStr = "ZAR";
				pay(amountStr, currencyStr, false);

			}
		});

	}

	// ----------------------------------------------------------------

	public void pay(String amount, String currency, boolean isTest) {
		fTrustPayApi.setTest(isTest);
		System.out.println("Prepare pay intent:" + amount + currency + "[*]"
				+ appuser + txDescription);
		Date now = new Date();
		
		Intent intent = fTrustPayApi.preparePayIntent("" + amount, currency,
				"[*]", appuser, txDescription,"DMO"+now.getTime());

		try {
			startActivityForResult(intent, 123);
		} catch (ActivityNotFoundException e) {
			TrustPayApi.downloadTrustPay(this);

		}
	}


	// This method is called when the billing is done
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		EditText t = (EditText) findViewById(R.id.etResult);
		if (requestCode == 123) {
			if (resultCode == Activity.RESULT_OK) {
				BillingResult res = (BillingResult) data
						.getSerializableExtra("result");
				t.setText("got result:" + res);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				if (data != null) {
					BillingResult res = (BillingResult) data
							.getSerializableExtra("result");
					t.setText("got result:" + res);
				}
			}
		} else if (requestCode == 321) {
			if (resultCode == Activity.RESULT_OK) {
				String res = data.getStringExtra("country");
				t.setText("got result:" + res);
			}
			
		} else {
			String res = "got strange result: (resultCode,requestCode):("
					+ resultCode + "," + requestCode + ")";
			t.setText(res);
		}
	}


	@Override
	public void onTrustPayPricePointsResult(PricePoints arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateComplete(Boolean arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
