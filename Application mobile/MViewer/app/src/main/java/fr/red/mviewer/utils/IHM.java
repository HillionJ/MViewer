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

    // Récupérer une Activité, un Fragment ou autre élément actif ou en sommeil
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

    // Ajouter une activité lors de son lancement
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

    // Récupérer l'activitée actuelle
    public AppCompatActivity getActiviteActive() {
        if (activiteActive.isDestroyed()) {
            // En cas d'activité inactive, chercher une potentiellement active
            for (Object ihmActive : ihmActives) {
                if (ihmActive instanceof AppCompatActivity && !((AppCompatActivity) ihmActive).isDestroyed()) {
                    activiteActive = (AppCompatActivity) ihmActive;
                    break;
                }
            }
        }
        return activiteActive;
    }

    // Récupérer une activité active
    public AppCompatActivity getActivite(Class<?> typeActivite) {
        return (AppCompatActivity) getIHMActive(typeActivite);
    }

    // Quitter une activité
    public void fermerActivite(Class<?> typeActivite) {
        Object activite = getIHMActive(typeActivite);
        if (activite instanceof AppCompatActivity) {
            ((AppCompatActivity)activite).finish();
        }
    }

    // Fermer les DialogFragment si existants
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

    // Lancer une nouvelle activité (sans extra)
    public void demarrerActivite(Object lanceur, Context contexte, Class<?> typeActivite) {
        Intent intent = new Intent(contexte, typeActivite);
        if (lanceur instanceof Fragment) {
            ((Fragment) lanceur).startActivity(intent);
        } else {
            ((AppCompatActivity) lanceur).startActivity(intent);
        }
    }

    // Mettre le mode sombre sur le haut du téléphone et sur le bas (les boutons)
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
