package com.example.queuemanagementapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.queuemanagementapp.Adapters.GalleryAdapter;
import com.example.queuemanagementapp.Adapters.ServicesAdapter;
import com.example.queuemanagementapp.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private NavController navControllerInfo;
    private NavController navControllerAdmin;

    private ScrollView mainContent;
    private FragmentContainerView navHostFragment;
    private FragmentContainerView navHostFragmentInfo;
    private FragmentContainerView navHostFragmentadmin;

    private Button btnAdminLogin;
    private Button btnBookAppointment; //make an appointment
    private Button btnAbout;
    private Button btnOpeningHours;
    private Button btnMyAppointments;
    private Button btnReviews;
    private Button dialButton; //call
    private Button btnOpenMaps; //maps


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navHostFragment = findViewById(R.id.nav_host_fragment);
        navHostFragmentInfo = findViewById(R.id.nav_host_fragment_info);
        navHostFragmentadmin = findViewById(R.id.nav_host_fragment_admin);

        // אתחול משתני ה-XML
        mainContent = findViewById(R.id.main_content);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);
        btnAbout = findViewById(R.id.btn_about);
        btnOpeningHours = findViewById(R.id.btn_opening_hours);
        btnMyAppointments = findViewById(R.id.btn_my_appointments);
        btnAdminLogin = findViewById(R.id.btn_admin_login);
        btnReviews= findViewById(R.id.btnReviews);
        btnOpenMaps = findViewById(R.id.btn_open_maps);
        dialButton = findViewById(R.id.dial_button);



        // אתחול הניווט הראשי
        NavHostFragment navHostMain = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostMain != null) {
            navController = navHostMain.getNavController();
        }

        // אתחול הניווט של מידע כללי
        NavHostFragment navHostInfo = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_info);
        if (navHostInfo != null) {
            navControllerInfo = navHostInfo.getNavController();
        }
        // אתחול NavController לכניסת מנהל
        NavHostFragment navHostAdmin = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_admin);
        if (navHostAdmin != null) {
            navControllerAdmin = navHostAdmin.getNavController();
        }


        //gallery pictures
        RecyclerView recyclerGallery = findViewById(R.id.recycler_gallery);
        recyclerGallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Integer> galleryImages = new ArrayList<>();
        galleryImages.add(R.drawable.haircut1);
        galleryImages.add(R.drawable.haircut2);
        galleryImages.add(R.drawable.haircut3);
        galleryImages.add(R.drawable.haircut4);
        galleryImages.add(R.drawable.haircut5);
        galleryImages.add(R.drawable.haircut6);
        galleryImages.add(R.drawable.haircut7);
        galleryImages.add(R.drawable.haircut8);
        galleryImages.add(R.drawable.haircut9);
        galleryImages.add(R.drawable.haircut10);

        GalleryAdapter galleryAdapter = new GalleryAdapter(galleryImages);
        recyclerGallery.setAdapter(galleryAdapter);

        //gallery services
        RecyclerView recyclerServices = findViewById(R.id.recycler_services);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this));
        List<String> services = new ArrayList<>();
        services.add("✂️ תספורת גברים - ₪70");
        services.add("💇‍♀️ תספורת נשים - ₪120");
        services.add("🎨 צביעת שיער - ₪200");
        services.add("🧴 החלקת שיער קרטין - ₪450");
        services.add("💆‍♀️ טיפולי שיקום שיער - ₪300");
        services.add("🖌 גוונים לשיער קצר - ₪250");
        services.add("🖌 גוונים לשיער ארוך - ₪350");
        services.add("💆‍♂️ שטיפת צבע - ₪100");
        services.add("🪮 עיצוב תסרוקות ערב - ₪300");


        ServicesAdapter servicesAdapter = new ServicesAdapter(services);
        recyclerServices.setAdapter(servicesAdapter);

        // ניווט ללחצן "קצת עלינו"
        btnAbout.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragmentInfo.setVisibility(View.VISIBLE);
            if (navControllerInfo != null) {
                navControllerInfo.navigate(R.id.aboutFragment);
            }
        });

        // ניווט ללחצן "שעות פתיחה"
        btnOpeningHours.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragmentInfo.setVisibility(View.VISIBLE);
            if (navControllerInfo != null) {
                navControllerInfo.navigate(R.id.openingHoursFragment);
            }
        });

        // ניווט ללחצן "התורים שלי"
        btnMyAppointments.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragmentInfo.setVisibility(View.VISIBLE);
            if (navControllerInfo != null) {
                navControllerInfo.navigate(R.id.myAppointmentsFragment);
            }
        });
        // ניווט ללחצן "ביקורות"

        btnReviews.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragmentInfo.setVisibility(View.VISIBLE);
            if (navControllerInfo != null) {
                navControllerInfo.navigate(R.id.loginReviewFragment);
            }
        });


        // ניווט ללחצן "קביעת תור"
        btnBookAppointment.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragment.setVisibility(View.VISIBLE);
            if (navController != null) {
                navController.navigate(R.id.enterPhoneFragment);
            }
        });

        // חיבור לכפתור כניסת מנהל

        btnAdminLogin.setOnClickListener(v -> {
            mainContent.setVisibility(View.GONE);
            navHostFragmentadmin.setVisibility(View.VISIBLE);
            if (navControllerAdmin != null) {
                navControllerAdmin.navigate(R.id.adminLoginFragment);
            }
        });
        // button for calling
        dialButton.setOnClickListener(v -> {
            String phoneNumber = "0534210224"; // המספר שברצונך לחייג אליו
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
        });

        // adress maps
        btnOpenMaps.setText("📍 רחוב דיזנגוף 280, תל אביב יפו, ישראל");
        btnOpenMaps.setOnClickListener(v -> {
            String address = "רחוב דיזנגוף 280, תל אביב יפו, ישראל"; // כתובת העסק
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address)); // יצירת URI לפתיחת הכתובת במפות
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // נסיון לפתוח בגוגל מפות

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent); // פותח את גוגל מפות אם האפליקציה מותקנת
            } else {
                // אם אין אפליקציית גוגל מפות, פותח את המיקום בדפדפן
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address)));
                startActivity(webIntent);
            }
        });

    }

    }

