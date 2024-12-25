class MainActivity
!!!137602.java!!!	onCreate(inout savedInstanceState : Bundle) : void
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        flowLayout = findViewById(R.id.idFlow);

        scrollView = findViewById(R.id.scrollView);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        currentIndex = 0;

        gestureDetector = new GestureDetector(this, new GestureListener());
        scrollView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        addPlaquette(null);

        scrollView.post(() -> {
            int screenWidth = scrollView.getWidth();
            int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
            paddingX = (screenWidth - plaquetteWidth) / 2;
        });

        try {
            MovieDB.foo(this);
        } catch (Exception e) {
            Log.e("_RED", "Erreur : " + e.getMessage());
        }
!!!137730.java!!!	addPlaquette(inout movie : Movie) : void
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_flow, flowLayout, false);

        if (movie != null) {
            // Afficher le titre
            TextView titre = itemView.findViewById(R.id.idTitrePlaquette);
            titre.setText(movie.getTitle());

            // Charger l'affiche du film
            ImageView image = itemView.findViewById(R.id.idImagePlaquette);

            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Log.d("_RED", posterUrl);
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable._1euctafoll)
                    .error(R.drawable._1euctafoll)
                    .into(image);
        }

        flowLayout.addView(itemView);
!!!138114.java!!!	scrollToNext() : void
        scrollView.smoothScrollTo(getXToScroll(Math.min(flowLayout.getChildCount() - 1, currentIndex + 1)), 0);
        currentIndex = Math.min(flowLayout.getChildCount() - 1, currentIndex + 1);
!!!138242.java!!!	scrollToPrevious() : void
        scrollView.smoothScrollTo(getXToScroll(Math.max(0, currentIndex - 1)), 0);
        currentIndex = Math.max(0, currentIndex - 1);
!!!138370.java!!!	snapToNearestPlaquette() : void
        int currentX = scrollView.getScrollX();
        int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
        int nearestPlaquette = Math.round((float) currentX / plaquetteWidth);
        int targetX = nearestPlaquette * plaquetteWidth;
        scrollView.smoothScrollTo(targetX, 0);
!!!138498.java!!!	getXToScroll(in targetID : int) : int
        int x = -paddingX;
        for (int i = 0; i < targetID; i++) {
            x += flowLayout.getChildAt(i).getWidth();
        }
        Log.d("_RED", targetID + ": " + x);
        return x;
!!!138626.java!!!	getCurrentPlaquette() : View
        return getPlaquette(getCurrentPlaquetteID());
!!!138754.java!!!	getPlaquette(in index : int) : View
        return flowLayout.getChildAt(index);
!!!138882.java!!!	getCurrentPlaquetteID() : int
        int scrollX = scrollView.getScrollX();

        // Largeur de l'écran (zone visible du ScrollView)
        int screenWidth = scrollView.getWidth();

        // Initialiser les valeurs pour la recherche
        int closestIndex = -1;
        int closestDistance = Integer.MAX_VALUE;

        // Parcourir toutes les plaquettes du FlowLayout
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            View plaquette = flowLayout.getChildAt(i);

            // Calculer la position X de la plaquette
            int plaquetteLeft = plaquette.getLeft();
            int plaquetteRight = plaquette.getRight();

            // Calculer la distance entre le centre de la plaquette et le centre de l'écran
            int plaquetteCenter = (plaquetteLeft + plaquetteRight) / 2;
            int screenCenter = scrollX + (screenWidth / 2);

            int distance = Math.abs(plaquetteCenter - screenCenter);

            // Trouver la plaquette la plus proche du centre
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        if (closestIndex != -1) {
            return closestIndex;
        }
        return 0;
