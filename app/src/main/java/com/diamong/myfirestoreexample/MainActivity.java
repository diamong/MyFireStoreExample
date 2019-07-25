package com.diamong.myfirestoreexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle, editTextDescription, editTextPriority;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My First Note");
    private CollectionReference notebookRef = db.collection("Notebook");

    private ListenerRegistration noteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edittext_title);
        editTextDescription = findViewById(R.id.edittext_description);
        editTextPriority = findViewById(R.id.edittext_priority);
        textViewData = findViewById(R.id.textviw_data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                } else {
                    String data = "";
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        String docId=note.getDocumentId();
                        String title = note.getTitle();
                        String desc = note.getDescription();
                        int priority=note.getPriority();

                        data += "ID: " + docId + "\nTitle: " + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
                    }

                    textViewData.setText(data);
                }
            }
        });
    }

    public void addNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString());

        Note note = new Note(title, description, priority);

        notebookRef.add(note);
    }

    public void loadNotes(View view) {
        notebookRef.whereGreaterThanOrEqualTo("priority",2)
                .whereLessThanOrEqualTo("priority",5)
                .orderBy("priority", Query.Direction.ASCENDING)
                .limit(5)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());

                    String docId = note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();
                    int priority = note.getPriority();

                    data += "ID: " + docId + "\nTitle: " + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
                }

                textViewData.setText(data);
            }
        });
    }
}
