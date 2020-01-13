package com.basewin.printdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.define.GlobalDef;
import com.basewin.models.PrintLine;
import com.basewin.models.TextPrintLine;
import com.basewin.services.ServiceManager;
import com.basewin.utils.AppUtil;
import com.basewin.utils.SaveBitmap;
import com.basewin.widgets.PrinterLayout;
import com.pos.sdk.accessory.PosAccessoryManager;
import com.pos.sdk.printer.PosPrintStateInfo;
import com.pos.sdk.printer.PosPrinter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PrintDemo";
    private Button print_android,print_layout,customPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView(){
        print_android = findViewById(R.id.print_android);
        print_layout = findViewById(R.id.print_layout);
        customPrint = findViewById(R.id.custom_print);
        print_android.setOnClickListener(this);
        print_layout.setOnClickListener(this);
        customPrint.setOnClickListener(this);
    }

    private void initData(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print_android:
                try {
                    ServiceManager.getInstence().getPrinter().setPrintTypesettingType(GlobalDef.ANDROID_TYPESETTING);
                    printData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_layout:
                try {
                    ServiceManager.getInstence().getPrinter().setPrintTypesettingType(GlobalDef.PRINTERLAYOUT_TYPESETTING);
                    printData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.custom_print:
                customLayout();
                break;
        }
    }

    private void printData() throws Exception {

        ServiceManager.getInstence().getPrinter().cleanCache();

        //set the custom font support two method,setPrintFont just support system font,
        //if system font can't meet your demand, you can use the setPrintFontByAsserts
//        ServiceManager.getInstence().getPrinter().setPrintFontByAsserts("consolas.ttf");
//        ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
//        ServiceManager.getInstence().getPrinter().setLineSpace(2);
        TextPrintLine textPrintLine = new TextPrintLine();
        textPrintLine.setContent("SmartPeak Printer Test");
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.CENTER);
        textPrintLine.setContent("--------------------------------");
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.LEFT);
        textPrintLine.setContent("Model: " + Build.MODEL);
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.LEFT);
        textPrintLine.setContent("Customer Name: " + AppUtil.getProp("ro.customer.name", "SmartPeak"));
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.LEFT);
        textPrintLine.setContent("DSN: " + PosAccessoryManager.getDefault().getVersion(PosAccessoryManager.VERSION_TYPE_DSN));
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.LEFT);
        textPrintLine.setContent("SoftWare Version: " + PosAccessoryManager.getDefault().getVersion(PosAccessoryManager.VERSION_TYPE_AP));
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(PrintLine.LEFT);
        // when you setPrintTypesettingType is  GlobalDef.PRINTERLAYOUT_TYPESETTING, the invert property will effective
        textPrintLine.setInvert(true);
        textPrintLine.setContent("SP Version: " + PosAccessoryManager.getDefault().getVersion(PosAccessoryManager.VERSION_TYPE_SP));
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(TextPrintLine.LEFT);
        textPrintLine.setInvert(false);
        String spVersion = PosAccessoryManager.getDefault().getVersion(PosAccessoryManager.VERSION_TYPE_SP);
        String spState = spVersion.substring(spVersion.length() - 2).trim();
        switch (spState) {
            case "1":
            case "2":
                spState = "Normal";
                break;
            case "0":
                spState = "Locked";
                break;
            case "3":
                spState = "Sensor Broken";
                break;
            default:
                break;
        }
        textPrintLine.setContent("SP Status: " + spState);
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setPosition(TextPrintLine.LEFT);
        textPrintLine.setContent("SDK FrameWork Version: " + PosAccessoryManager.getDefault().getVersion(PosAccessoryManager.VERSION_TYPE_SDK));
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        textPrintLine.setContent("\n\n\n\n");
        ServiceManager.getInstence().getPrinter().addPrintLine(textPrintLine);

        ServiceManager.getInstence().getPrinter().beginPrint(new OnPrinterListener() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onFinish() {
                Log.e(TAG, "print finish ！");
            }

            @Override
            public void onStart() {

            }
        });
    }


    /**
     * If you need more complex typography, you can refer to this section
     * */
    private void customLayout(){
        PrinterLayout printView = (PrinterLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_print_template,null);
        printView.viewToBitmap(new PrinterLayout.ViewToBitmapListener() {
            @Override
            public void success(Bitmap bitmap) {
                if(bitmap==null) {
                    failure();
                    return;
                }
                try {
                    byte[] data = SaveBitmap.bitmapToByteArray(bitmap,false);
                    int num = PosPrinter.getNumberOfPrinters();
                    if(num>0){
                        PosPrinter posPrinter = PosPrinter.open();
                        posPrinter.setOnEventListener(new PosPrintListener());
                        //设置打印参数
                        PosPrinter.Parameters param = posPrinter.getParameters();
                        param.setPrintAlign(PosPrinter.Parameters.ALIGH_CENTER);
                        posPrinter.setParameters(param);
                        //清除打印缓存
                        posPrinter.cleanCache();
                        posPrinter.addRawDataToCache(data);
                        posPrinter.print();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure() {
                Log.e(TAG, "get bitmap failure");
            }
        });
    }

    private class PosPrintListener implements PosPrinter.EventListener{

        @Override
        public void onInfo(PosPrinter posPrinter, int i, int i1) {
            PosPrintStateInfo statInfo = new PosPrintStateInfo();
            posPrinter.getPrintStateInfo(statInfo);
            int printState = statInfo.mState;

            switch (printState) {
                case PosPrinter.PRINTER_STATE_IDLE: {
                    Log.e("feeling", "print finish ！");
                }
            }
        }

        @Override
        public void onError(PosPrinter posPrinter, int i, int i1) {

        }

        @Override
        public void onCursorChanged(PosPrinter posPrinter, int i, int i1, int i2, int i3) {

        }
    }
}
