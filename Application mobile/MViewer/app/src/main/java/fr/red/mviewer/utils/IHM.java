package fr.red.mviewer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class IHM {

    private static IHM ihm;
    private List<Object> ihmActives = new ArrayList<>();
    private AppCompatActivity activiteActive = null;

    @Nullable
    public Object getIHMActive(Class<?> typeObjet) {
        for (Object ihmActive : ihmActives) {
            if (typeObjet.isInstance(ihmActive)) {
                return ihmActive;
            }
        }
        return null;
    }

    public static IHM getIHM() {
        return IHM.ihm;
    }

    public IHM(AppCompatActivity activiteActive) {
        IHM.ihm = this;
        this.activiteActive = activiteActive;
    }

    public void ajouterIHM(Object pageIHM) {
        if (pageIHM instanceof AppCompatActivity) {
            activiteActive = (AppCompatActivity) pageIHM;
        }
        for (Object ihmActive : ihmActives) {
            if (pageIHM.getClass() == ihmActive.getClass()) {
                ihmActives.remove(ihmActive);
                break;
            }
        }
        ihmActives.add(pageIHM);
    }

    public AppCompatActivity getActiviteActive() {
        return activiteActive;
    }

    public AppCompatActivity getActivite(Class<?> typeActivite) {
        return (AppCompatActivity) getIHMActive(typeActivite);
    }

    public void fermerActivite(Class<?> typeActivite) {
        Object activite = getIHMActive(typeActivite);
        if (activite instanceof AppCompatActivity) {
            ((AppCompatActivity)activite).finish();
        }
    }

    public void fermerPopups() {
        for (Object ihmActive : ihmActives) {
            if (ihmActive instanceof DialogFragment) {
                try {
                    ((DialogFragment)ihmActive).dismiss();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void demarrerActivite(Object lanceur, Context contexte, Class<?> typeActivite) {
        Intent intent = new Intent(contexte, typeActivite);
        if (lanceur instanceof Fragment) {
            ((Fragment) lanceur).startActivity(intent);
        } else {
            Log.d("_RED", lanceur.getClass().getSimpleName() + " - " + typeActivite.getName()+
                     " - " + lanceur.getClass().getCanonicalName());
            ((AppCompatActivity) lanceur).startActivity(intent);
        }
    }

    public void applyDarkTheme() {
        Window window = getActiviteActive().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //Mettre le fond en noir
        int colorCodeDark = Color.parseColor("#000000");
        window.setStatusBarColor(colorCodeDark);
        window.setNavigationBarColor(colorCodeDark);
        WindowInsetsController insetsController = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        }
    }
}
