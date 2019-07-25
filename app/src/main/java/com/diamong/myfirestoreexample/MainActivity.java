package com.diamong.myfirestoreexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle, editTextDescription;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My First Note");

    private ListenerRegistration noteListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edittext_title);
        editTextDescription = findViewById(R.id.edittext_description);
        textViewData = findViewById(R.id.textviw_data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Toast.makeText(MainActivity.this, "Error while loading...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,e.toString());
                    return;
                }
                if (documentSnapshot.exists()) {
                    Note note =documentSnapshot.toObject(Note.class);
                    String title = note.getTitle();
                    String description=note.getDescription();

                    textViewData.setText("Title" + title + "\n" + "Description: " + description);
                } else {
                    textViewData.setText("No Exists");
                }
            }
        });
    }



    public void saveNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        /*HashMap<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION, description);*/
        Note note = new Note(title,description);

        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });


    }

    public void loadNote(View view) {
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    /*String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    //HashMap<String,Object> note = documentSnapshot.getData();*/

                    Note note =documentSnapshot.toObject(Note.class);
                    String title = note.getTitle();
                    String description=note.getDescription();

                    textViewData.setText("Title" + title + "\n" + "Description: " + description);
                } else {
                    Toast.makeText(MainActivity.this, "Document does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });
    }

    public void updateDescription(View view) {
        String desc = editTextDescription.getText().toString();
        /*Map<String,Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION,desc);*/

        //noteRef.set(note, SetOptions.merge());
        noteRef.update(KEY_DESCRIPTION,desc);
    }

    public void deleteDescription(View view) {
        //Map<String,Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());

        //noteRef.update(note);
        noteRef.update(KEY_DESCRIPTION,FieldValue.delete());
    }

    public void deletenote(View view) {
        noteRef.delete();
    }
}
