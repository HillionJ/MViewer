format 224

classcanvas 128002 class_ref 134530 // Utilisateur
  simpleclassdiagramsettings end
  xyz 24 486 2000
end
usecasecanvas 128130 usecase_ref 134530 // Visualiser le flux
  xyzwh 402 662 3005 64 32 label_xy 395 694
end
usecasecanvas 128258 usecase_ref 134658 // Effectuer une recherche
  xyzwh 402 502 3005 64 32 label_xy 375 534
end
usecasecanvas 128386 usecase_ref 134786 // Entrer le nom du film
  xyzwh 691 555 3005 64 32 label_xy 675 587
end
usecasecanvas 128514 usecase_ref 134914 // Choisir un filtre
  xyzwh 691 438 3005 64 32 label_xy 688 470
end
usecasecanvas 128642 usecase_ref 135042 // Param�trer l'application
  xyzwh 402 827 3005 64 32 label_xy 379 859
end
usecasecanvas 131202 usecase_ref 135554 // D�connecter l'utilisateur
  xyzwh 691 873 3005 64 32 label_xy 666 905
end
usecasecanvas 131330 usecase_ref 135682 // Choisir le th�me de l'application
  xyzwh 691 749 3005 64 32 label_xy 649 781
end
usecasecanvas 131842 usecase_ref 135938 // Entrer un mot de passe
  xyzwh 517 356 3005 64 32 label_xy 494 388
end
usecasecanvas 131970 usecase_ref 135170 // Connecter l'utilisateur
  xyzwh 216 322 3005 64 32 label_xy 197 354
end
usecasecanvas 132226 usecase_ref 136066 // Entrer une adresse email
  xyzwh 517 253 3005 64 32 label_xy 490 285
end
line 129794 ----
  from ref 128002 z 3006 to ref 128130
line 129922 ----
  from ref 128002 z 3006 to ref 128258
line 130050 ----
  from ref 128002 z 3006 to ref 128642
simplerelationcanvas 130690 simplerelation_ref 134658
  from ref 128258 z 3006 stereotype "<<include>>" xyz 541 543 3000 to ref 128386
end
simplerelationcanvas 130818 simplerelation_ref 134786
  from ref 128514 z 3006 stereotype "<<extend>>" xyz 542 483 3000 to ref 128258
end
simplerelationcanvas 131458 simplerelation_ref 134914
  from ref 131330 z 3006 stereotype "<<extend>>" xyz 542 801 3000 to ref 128642
end
simplerelationcanvas 131586 simplerelation_ref 135042
  from ref 131202 z 3006 stereotype "<<extend>>" xyz 542 864 3000 to ref 128642
end
line 132098 ----
  from ref 128002 z 3006 to ref 131970
simplerelationcanvas 132354 simplerelation_ref 135170
  from ref 131970 z 3006 stereotype "<<include>>" xyz 361 301 3000 to ref 132226
end
simplerelationcanvas 132482 simplerelation_ref 135298
  from ref 131970 z 3006 stereotype "<<include>>" xyz 361 353 3000 to ref 131842
end
simplerelationcanvas 132610 simplerelation_ref 135426
  from ref 128642 z 3006 stereotype "<<include>>" xyz 366 752 3000 to ref 131970
end
simplerelationcanvas 132738 simplerelation_ref 135554
  from ref 128258 z 3006 stereotype "<<include>>" xyz 313 426 3000 to ref 131970
end
simplerelationcanvas 132866 simplerelation_ref 135682
  from ref 128130 z 3006 stereotype "<<include>>" xyz 370 607 3000 to ref 131970
end
simplerelationcanvas 132994 simplerelation_ref 134530
  from ref 128258 z 3006 to ref 128386
end
end