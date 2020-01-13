package com.coupne.bosniaguide;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    public static int ADMIN_ACTION = -1, ADD = 1, UPDATE = 2, DELETE = 3;
    private RelativeLayout mainContent;
    private RelativeLayout siginContent;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContent = findViewById(R.id.main_content);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        LandmarksContent();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_dashboard) {

            DashboardContent();

        } else*/ if (id == R.id.nav_landmarks) {

            LandmarksContent();

        } else if (id == R.id.nav_cities) {

            CitiesContent();

        } else if (id == R.id.nav_activities) {

            ActivitiesContent();

        } else if (id == R.id.nav_login) {

            LoginContent();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgress;

    private EditText reg_fristName;
    private EditText reg_lastname;
    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirmPass;
    private Button reg_btn;
    private ProgressBar reg_progresss;
    private Marker myMarker;

    boolean isAdminType = false;
    private void LoginContent() {
        setTitle("Login");

        mainContent.removeAllViews();
        final View view = getLayoutInflater().inflate(R.layout.activity_signin, null);

        loginEmailText = view.findViewById(R.id.login_email);
        loginPassText = view.findViewById(R.id.login_pass);
        loginBtn = view.findViewById(R.id.login_btn);
        loginRegBtn = view.findViewById(R.id.reg_btn);
        loginProgress = view.findViewById(R.id.login_progress);
        RadioGroup rgUserType = view.findViewById(R.id.rgUserType);

        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if ( i == R.id.rbUser) {
                    isAdminType = false;
                    loginRegBtn.setVisibility(View.VISIBLE);
                } else if ( i == R.id.rbAdmin) {
                    isAdminType = true;
                    loginRegBtn.setVisibility(View.GONE);
                }
            }
        });


        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTitle("Register");

                mainContent.removeAllViews();

                View view = getLayoutInflater().inflate(R.layout.activity_signup, null);

                reg_fristName = view.findViewById(R.id.reg_fname);
                reg_lastname = view.findViewById(R.id.reg_lname);
                reg_email = view.findViewById(R.id.reg_email);
                reg_pass = view.findViewById(R.id.reg_pass);
                reg_confirmPass = view.findViewById(R.id.reg_confirmPass);
                reg_btn = view.findViewById(R.id.reg_new_btn);
                reg_progresss = view.findViewById(R.id.reg_progress);

                reg_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String firstname = reg_fristName.getText().toString();
                        String lastname = reg_lastname.getText().toString();
                        String email = reg_email.getText().toString();
                        String pass = reg_pass.getText().toString();
                        String confirmpass = reg_confirmPass.getText().toString();

                        if (!firstname.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !confirmpass.isEmpty()) {

                            if (pass.equals(confirmpass)) {

                                reg_progresss.setVisibility(View.VISIBLE);

                                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            Toast toast = Toast.makeText(MainActivity.this, "Register Successful", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
//                                            sendToMain();

                                        } else {

                                            String errorMessage = task.getException().getMessage();
                                            Toast toast = Toast.makeText(MainActivity.this, "Eror: " + errorMessage, Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                        }

                                        reg_progresss.setVisibility(View.INVISIBLE);

                                    }
                                });

                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "Confirme Password is wrong!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, "Input your detail correctly!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

                mainContent.addView(view);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String loginEmail = loginEmailText.getText().toString().trim();
                final String loginPass = loginPassText.getText().toString().trim();

                if (isAdminType) {
                    if (!TextUtils.isEmpty(loginEmail)
                            && !TextUtils.isEmpty(loginPass)
                            && TextUtils.equals("hakim@hotmail.com", loginEmail)
                            && TextUtils.equals("123123123", loginPass)){
                        DahsboardHome();
                    }else{
                        Toast.makeText(MainActivity.this, "Input valid Email and Password", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }



                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                new Session(MainActivity.this).setusename(loginEmail);
                                Toast toast = Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            } else {

                                String e = task.getException().getMessage();
                                Toast toast = Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }

                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {

                    Toast toast = Toast.makeText(MainActivity.this, "Input Email and Password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            }
        });

        mainContent.addView(view);
    }

    private RecyclerView recyclerView;

    private int[] images_LandMarks = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3
    };
    private String[] str_LandMarks = {
            "Mountains",
            "Rivers",
            "CoffeeShops",
            "Restaurants",
            "Gardens",
            "ShoppingMalls"
    };
    private int[] images_Cities = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic1,
            R.drawable.pic2
    };
    private String[] str_Cities = {
            "Sarayevo",
            "Mostar",
            "Tuzla",
            "Bihac",
            "Banja Luka"
    };
    private int[] images_Activities = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic1
    };
    private String[] str_Activities = {
            "Football",
            "Rafting",
            "Running",
            "Swimming"
    };

    private Button dashboard_btn_login;

    private void DashboardContent() {

        setTitle("Dashboard");

        mainContent.removeAllViews();

        final View view = getLayoutInflater().inflate(R.layout.content_dashboard, null);

        dashboard_btn_login = view.findViewById(R.id.dashboard_btn_login);
        dashboard_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DahsboardHome();

            }
        });

        mainContent.addView(view);
    }

    private Spinner spn_place;
    private GoogleMap mMap;
    private EditText add_name;
    private EditText add_description;
    private EditText add_address;
    private Button add_photo_btn;
    private Button btnSubmit;
    private Button btnCancel;
    private ProgressBar mProgressBar;
    private ImageView add_imageview;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    String strCategory = "";

    LinearLayout mainLayout, btnLayout;
    Button add, delete, update;

    private void DahsboardHome() {

        mainContent.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.content_dashboard_home, null);
        btnLayout = view.findViewById(R.id.btnLayout);
        btnLayout.setVisibility(View.VISIBLE);
        add = view.findViewById(R.id.btnAdd);
        update = view.findViewById(R.id.btnUpdate);
        delete = view.findViewById(R.id.btnDelete);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ADMIN_ACTION = ADD;
                btnLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ADMIN_ACTION = UPDATE;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                startActivity(intent);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ADMIN_ACTION = DELETE;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);
        spn_place = view.findViewById(R.id.place_cate);
        add_name = view.findViewById(R.id.add_name);
        add_description = view.findViewById(R.id.add_description);
        add_address = view.findViewById(R.id.add_address);
        add_photo_btn = view.findViewById(R.id.add_choose);
        btnSubmit = view.findViewById(R.id.submit_btn);
        btnCancel = view.findViewById(R.id.cancel_btn);
        add_imageview = view.findViewById(R.id.add_imageview);
        mProgressBar = view.findViewById(R.id.dashboard_progress);


        ArrayList<CharSequence> places = new ArrayList<>();

        for (int i = 0; i < str_LandMarks.length; i++) {

            places.add(str_LandMarks[i]);

        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, places);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_place.setAdapter(adapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dashboard_map);
        mapFragment.getMapAsync(this);


        spn_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strCategory = str_LandMarks[position];
//                Toast.makeText(MainActivity.this, strCategory, Toast.LENGTH_SHORT).show();

                mStorageRef = FirebaseStorage.getInstance().getReference("LandMarks/" + strCategory);
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("LandMarks/" + strCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                strCategory = str_LandMarks[0];
//                Toast.makeText(MainActivity.this, strCategory, Toast.LENGTH_SHORT).show();

                mStorageRef = FirebaseStorage.getInstance().getReference("LandMarks/" + strCategory);
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("LandMarks/" + strCategory);

            }
        });

        add_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {

                    Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();

                } else {

                    upload();

                }
            }
        });

        mainContent.addView(view);

    }

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;

    private void LandmarksContent() {

        setTitle("LandMarks");

        mainContent.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.content_landmarks, null);

        recyclerView = view.findViewById(R.id.recycler_viewAll);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images_LandMarks, str_LandMarks, "LandMarks", this);
        recyclerView.setAdapter(adapter);

        mainContent.addView(view);

    }

    private void CitiesContent() {
        setTitle("Cities");

        mainContent.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.content_landmarks, null);

        recyclerView = view.findViewById(R.id.recycler_viewAll);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images_Cities, str_Cities, "Cities", this);
        recyclerView.setAdapter(adapter);

        mainContent.addView(view);
    }

    private void ActivitiesContent() {
        setTitle("Activities");

        mainContent.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.content_landmarks, null);

        recyclerView = view.findViewById(R.id.recycler_viewAll);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images_Activities, str_Activities, "Activities", this);
        recyclerView.setAdapter(adapter);

        mainContent.addView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
//            sendToLogin();
        }
    }


    public void logout() {
        mAuth.signOut();
        new Session(MainActivity.this).setusename("");
//        sendToLogin();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void upload() {

        mProgressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            final UploadTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();

                            }
                            // Continue with the task to get the download URL
                            return fileReference.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {


                                PlaceData placeData = new PlaceData(
                                        add_address.getText().toString(),
                                        myMarker.getPosition().latitude,
                                        myMarker.getPosition().longitude,
                                        add_description.getText().toString(),
                                        add_name.getText().toString(),
                                        task.getResult().toString(), null);///////////////////////////////////////////////
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(placeData);

                                Toast.makeText(MainActivity.this, "Upload succesfull", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);


                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

//            mUploadTask = fileReference.putFile(imageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//                                }
//                            }, 500);
//
//                            PlaceData placeData = new PlaceData(
//                                    add_address.getText().toString(),
//                                    "51.508306",
//                                    "-0.075864",
//                                    add_description.getText().toString(),
//                                    add_name.getText().toString(),
//                                    taskSnapshot.getStorage().getDownloadUrl().toString());///////////////////////////////////////////////
//                            String uploadId = mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(placeData);
//
//                            Toast.makeText(MainActivity.this, "Upload succesfull", Toast.LENGTH_SHORT).show();
//                            mProgressBar.setVisibility(View.INVISIBLE);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /taskSnapshot
//                            .getTotalByteCount());
//                            mProgressBar.setProgress((int)progress);
//
//                        }
//                    });

        } else {
            Toast.makeText(MainActivity.this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            add_imageview.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUri).into(add_imageview);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng bosnia = new LatLng(43.9159, 17.6791);
        myMarker = mMap.addMarker(new MarkerOptions().position(bosnia).title("This is Bosnia."));
        myMarker.setDraggable(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bosnia));
    }
}
