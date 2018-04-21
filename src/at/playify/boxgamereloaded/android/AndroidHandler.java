package at.playify.boxgamereloaded.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.interfaces.Handler;


class AndroidHandler extends Handler {
    private GameActivity a;
    private AlertDialog ad;

    AndroidHandler(GameActivity a) {
        this.a=a;
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
        ContextThemeWrapper context;
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
    public File baseDir(){
        return a.getExternalFilesDir(null);
    }
}
