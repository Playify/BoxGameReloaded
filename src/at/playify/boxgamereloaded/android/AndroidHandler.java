package at.playify.boxgamereloaded.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.interfaces.Handler;


public class AndroidHandler implements Handler {
    private GameActivity a;
    private InputMethodManager in;
    private boolean keybd;
    private AlertDialog ad;

    AndroidHandler(GameActivity a) {
        this.a=a;
        in=((InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    public boolean isKeyboardVisible() {
        return false;
    }

    @Override
    public void setKeyboardVisible(final boolean b) {
        if (b) {
            if (ad!=null)return;
        }else{
            if (ad!=null) {
                ad.dismiss();
                ad=null;
                return;
            }
        }
        ContextThemeWrapper context=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context=new ContextThemeWrapper(a, android.R.style.Theme_Material_Dialog);
        }else{
            context=a;
        }
        final EditText input = new EditText(context);
        input.setText("/");
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_DONE);
        input.setSingleLine();
        ad = new AlertDialog.Builder(context).setTitle("CMD" + (": /")).setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        a.game.runcmd(input.getText().toString());
                        dialog.dismiss();
                        a.game.pauseLock.unlock();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ad=null;
                    }
                }).show();
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    a.game.runcmd(input.getText().toString());
                    ad.dismiss();
                    a.game.pauseLock.unlock();
                    return true;
                }
                return false;
            }
        });
        input.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    a.game.runcmd(input.getText().toString());
                    ad.dismiss();
                    a.game.pauseLock.unlock();
                    return true;
                }
                return false;
            }
        });
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.game.runcmd(input.getText().toString());
                ad.dismiss();
                a.game.pauseLock.unlock();
            }
        });
        input.setSelection(1);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i=0; i<s.length(); i++) {
                    if (!Game.allowedChars.contains(s.charAt(i)+"")) {
                        s.delete(i,i+1);
                        i--;
                    }
                }
                ad.setTitle(TextUtils.concat("CMD" + (s.length() == 0 ? "" : ": "), s.toString()));
            }
        });
    }

    @Override
    public String getClipboardString() {
        return null;
    }

    @Override
    public void setClipboardString(String s) {

    }

    @Override
    public JSONObject read(String filename) {
        try {
            File file = new File(a.getExternalFilesDir(null), filename + ".json");
            try(Scanner sc = new Scanner(file)) {
                StringBuilder str=new StringBuilder();
                while (sc.hasNextLine()) {
                    str.append(sc.nextLine()).append("\n");
                }
                sc.close();
                JSONTokener tokener=new JSONTokener(str.toString());
                return new JSONObject(tokener);
            }
        }catch (FileNotFoundException e){
            return new JSONObject();
        }catch (Exception e){
            e.printStackTrace();
            return new JSONObject();
        }
    }

    @Override
    public void write(String filename, JSONObject o) {
        File file = new File(a.getExternalFilesDir(null), filename + ".json");
        try{
            if (!file.exists()) {
                file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter(file)){
            fw.write(o.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
