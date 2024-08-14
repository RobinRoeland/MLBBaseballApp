package com.example.baseballapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.baseballapp.classes.MLB.MLBTeamInfo;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.functionlib.LocaleFileLib;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.example.baseballapp.databinding.ActivityPaypallProcessCompleteBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaypallProcessCompleteActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPaypallProcessCompleteBinding binding;
    private String orderID;
    private MLBDataLayer repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MLBTicket ticket = MLBDataLayer.getInstance().m_tempTicketDuringPayment;
        super.onCreate(savedInstanceState);
        repo = MLBDataLayer.getInstance();

        binding = ActivityPaypallProcessCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //get the orderID from the query parameter
        Uri redirectUri = getIntent().getData();
        List<String> segmentsInUrl = redirectUri.getPathSegments();
        //hier kan je succes of failure halen uit de segmenstInURL
        orderID = redirectUri.getQueryParameter("token");
        String payerID = redirectUri.getQueryParameter("PayerID");

        binding.relativeLayoutSuccess.setVisibility(View.GONE);
        binding.relativeLayoutFail.setVisibility(View.GONE);
        binding.paypallProcessProgress.setVisibility(View.GONE);

        String confirmDetails = String.format("You're about to pay %.02f€", ticket.ticketPrice) + "\n" +
                "Your seat will be: " + ticket.venueSeat + " in box " + ticket.venueBox + "(" + ticket.venueZone + ")";
        binding.relativeLayoutConfirmDetails.setText(confirmDetails);

        //Stap 1: Annuleren of bevestigen order
        binding.relativeLayoutConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureOrder(orderID);
            }
        });

        binding.relativeLayoutCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToOrderZonderConfirm();
            }
        });

        //Stap 2: terugspringen naar activity na betaling of annuleren
        binding.relativeLayoutJumpToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaypallProcessCompleteActivity.this, MainActivity.class);
                intent.putExtra("JumpToFragment","TicketSuccess");
                intent.putExtra("AfgeslotenOrderId",orderID);
                //intent.putExtra("AfgeslotenPaymentId",mandje.mPayPalPaymentId);


                startActivity(intent);
            }
        });

        binding.relativeLayoutBackToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToOrderZonderConfirm();
            }
        });

    }

    void captureOrder(String orderID){
        //get the accessToken from MainActivity
        binding.paypallProcessProgress.setVisibility(View.VISIBLE);

        String accessToken = repo.m_PaymentSessionToken_Paypal;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + accessToken);

        client.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderID+"/capture", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i("RESPONSE", response);
                showFailedState(response);
                binding.paypallProcessProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // eerst het resultaat van call verwerken om paymentid op te halen
                String paymentId = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String orderId = jsonResponse.getString("id"); // This is the order ID
                    JSONArray purchaseUnits = jsonResponse.getJSONArray("purchase_units");
                    if (purchaseUnits.length() > 0) {
                        JSONObject purchaseUnit = purchaseUnits.getJSONObject(0);
                        JSONArray payments = purchaseUnit.getJSONObject("payments").getJSONArray("captures");
                        if (payments.length() > 0) {
                            JSONObject payment = payments.getJSONObject(0);
                            paymentId = payment.getString("id"); // dit is de payment id
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //payment succesfull, now gen qr code and make ticket permanent in datalayer list + store to db
                //make qr code
                generateQRcodeForTicket();
                //add ticket in repo list
                List<MLBTicket> repo_ticketlist = repo.m_TicketList.getValue();
                repo_ticketlist.add(repo.m_tempTicketDuringPayment);
                repo.m_TicketList.postValue(repo_ticketlist);
                //save to roomdb
                repo.saveNewTicketInRoomDB(repo.m_tempTicketDuringPayment, getBaseContext());

                // einde paypal access token life
                repo.m_PaymentSessionToken_Paypal = "";
                // Success !!! toon cardview succes
                showSuccessState();
                //end life of tempticket
                //repo.m_tempTicketDuringPayment = null;
                binding.paypallProcessProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showFailedState(String response) {
        binding.relativeLayoutSuccess.setVisibility(View.GONE);
        binding.relativeLayoutConfirm.setVisibility(View.GONE);
        binding.relativeLayoutFail.setVisibility(View.VISIBLE);
    }

    private void showSuccessState() {
        Team home = repo.m_tempTicketDuringPayment.mlbGame.getHomeTeam(repo);
        Team away =  repo.m_tempTicketDuringPayment.mlbGame.getAwayTeam(repo);

        binding.dialogScheduleHometeam.setText(home.name_display_long);
        binding.dialogScheduleAwayteam.setText(away.name_display_long);
        //Home Team image
        WebFetchImageTask webTaskHome = new WebFetchImageTask(getBaseContext());
        webTaskHome.m_image = binding.dialogScheduleHometeamImage;
        webTaskHome.execute(home);

        //Away Team image
        WebFetchImageTask webTaskAway = new WebFetchImageTask(getBaseContext());
        webTaskAway.m_image = binding.dialogScheduleAwayteamImage;
        webTaskAway.execute(away);

        String homeTeamStartTime = repo.m_tempTicketDuringPayment.getFormattedStartTime(repo);

        binding.dialogTicketDate.setText("Game date " + homeTeamStartTime + " at " +  repo.m_tempTicketDuringPayment.mlbGame.venue.name);
        String previewSeating = "Zone: " + repo.m_tempTicketDuringPayment.venueZone + "\nBox: " + repo.m_tempTicketDuringPayment.venueBox + "\n Seat: " + repo.m_tempTicketDuringPayment.venueSeat;
        binding.dialogTicketPreview.setText(previewSeating);
        binding.relativeLayoutSuccess.setVisibility(View.VISIBLE);
        binding.relativeLayoutFail.setVisibility(View.GONE);
        binding.relativeLayoutConfirm.setVisibility(View.GONE);
    }

    private void generateQRcodeForTicket() {
        Team home = repo.m_tempTicketDuringPayment.mlbGame.getHomeTeam(repo);
        Team away = repo.m_tempTicketDuringPayment.mlbGame.getAwayTeam(repo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        LocalDateTime parsedDateTime = LocalDateTime.parse(repo.m_tempTicketDuringPayment.mlbGame.gameDate, formatter);
        // Convert LocalDateTime to ZonedDateTime with UTC time zone
        ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(parsedDateTime, ZoneId.of("UTC"));

        DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern("yyyyMMdd");
        // Format the LocalDateTime to a string
        String formattedDate = parsedDateTime.format(formatterOut);
        Bitmap qrCodeBitmap = null;
        String qrCodeText = formattedDate + "/" + home.mlb_org_abbrev + "_" + away.mlb_org_abbrev + "/" + repo.m_tempTicketDuringPayment.mlbGame.venue.name;
        String qrFileName = formattedDate + "_" + home.mlb_org_abbrev + "vs" + away.mlb_org_abbrev + ".bmp";
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(qrCodeText, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeBitmap = barcodeEncoder.createBitmap(bitMatrix);
            //qrCodeImageView.setImageBitmap(qrCodeBitmap);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
        if(qrCodeBitmap != null) {
            LocaleFileLib.saveBitmapToLocalFile(qrCodeBitmap, "images/tickets", qrFileName, getBaseContext());
            // attach filename to ticket
            MLBDataLayer.getInstance().m_tempTicketDuringPayment.m_imageName = qrFileName;
            MLBDataLayer.getInstance().m_tempTicketDuringPayment.m_localFileSubFolder = "images/tickets";
            MLBDataLayer.getInstance().m_tempTicketDuringPayment.m_fullImageURL = "OnlyLocalLoading";
            MLBDataLayer.getInstance().m_tempTicketDuringPayment.m_image = qrCodeBitmap;
        }
    }

    public void returnToOrderZonderConfirm() {
        Intent intent = new Intent(PaypallProcessCompleteActivity.this, MainActivity.class);
        intent.putExtra("JumpToFragment","AnnulerenTicket");
        startActivity(intent);
    }
}