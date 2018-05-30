package at.playify.boxgamereloaded.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.util.Action;

import static android.content.Context.CLIPBOARD_SERVICE;


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
            if (ad!=null) return;
        } else {
            if (ad!=null) {
                ad.dismiss();
                ad=null;
                return;
            }
        }
        ContextThemeWrapper context;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context=new ContextThemeWrapper(a, android.R.style.Theme_Material_Dialog);
        } else {
            context=a;
        }
        final EditText input=new EditText(context);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE);
        input.setSingleLine();
        ad=new AlertDialog.Builder(context).setTitle("CMD: ").setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runcmd(input.getText().toString());
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
                if (actionId==EditorInfo.IME_ACTION_DONE) {
                    runcmd(input.getText().toString());
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
                if (event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    runcmd(input.getText().toString());
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
                runcmd(input.getText().toString());
                ad.dismiss();
                a.game.pauseLock.unlock();
            }
        });
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
                        s.delete(i, i+1);
                        i--;
                    }
                }
                ad.setTitle(TextUtils.concat("CMD: ", s.toString()));
            }
        });
    }

    @Override
    public File baseDir(String s) {
        return a.getExternalFilesDir(null);
    }

    @Override
    public InputStream asset(String s) {
        try {
            return a.getAssets().open(s);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getClipboard() {
        ClipboardManager clipboard=(ClipboardManager) a.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip=clipboard.getPrimaryClip();
        if (clip!=null) {
            return clip.getItemAt(0).getText().toString();
        } else {
            return "";
        }
    }

    @Override
    public void setClipboard(String s) {
        ClipboardManager clipboard=(ClipboardManager) a.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("BoxGameReloaded Level", s));
    }

    private void runcmd(final String s) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                a.game.cmd.run(s);
            }
        });
        thread.setName("Command Executor");
        thread.start();
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public void keybd(String title, boolean pw,String preEnteredText, final Action.Bool<String> action) {
        ContextThemeWrapper context;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context=new ContextThemeWrapper(a, android.R.style.Theme_Material_Dialog);
        } else {
            context=a;
        }
        final Dialog dialog=new Dialog(context);
        RelativeLayout content=new RelativeLayout(context);
        final EditText text=new EditText(context);
        content.addView(text, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setSingleLine();
        text.setText(preEnteredText);
        text.setSelection(preEnteredText.length());
        if (pw){
            text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            text.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        text.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==EditorInfo.IME_ACTION_DONE||event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    if (action.exec(text.getText().toString())) {
                        dialog.dismiss();
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });

        dialog.addContentView(content, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dlg) {
                text.requestFocus();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        dialog.setTitle(title);
        dialog.show();
    }
}
