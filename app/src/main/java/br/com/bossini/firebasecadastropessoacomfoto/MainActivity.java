package br.com.bossini.firebasecadastropessoacomfoto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private StorageReference rootStorageRef;
    private DatabaseReference rootDatabaseRef;
    private EditText cpfEditText, nomeEditText, idadeEditText;
    private ImageView fotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cpfEditText = (EditText) findViewById(R.id.cpfEditText);
        nomeEditText = (EditText) findViewById(R.id.nomeEditText);
        idadeEditText = (EditText) findViewById(R.id.idadeEditText);
        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cpf = cpfEditText.getEditableText().toString();
                String nome = nomeEditText.getEditableText().toString();
                int idade = Integer.parseInt(idadeEditText.getEditableText().toString());
                final Pessoa pessoa = new Pessoa (cpf, nome, idade);
                //teste
                Bitmap bitmap = ((BitmapDrawable) fotoImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);

                StorageReference imgRef = rootStorageRef.child("img");
                imgRef.child(cpf).putBytes(bos.toByteArray()).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests")
                                String fotoURL = taskSnapshot.getDownloadUrl().toString();
                                pessoa.setFotoURL(fotoURL);
                                rootDatabaseRef.child(pessoa.getCpf()).setValue(pessoa);
                            }
                        }

                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }
        });
        rootStorageRef = FirebaseStorage.getInstance().getReference();
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        rootDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (iterator.hasNext()){
                    Pessoa pessoa = iterator.next().getValue(Pessoa.class);
                    if (pessoa != null) {
                        cpfEditText.setText(pessoa.getCpf());
                        nomeEditText.setText(pessoa.getNome());
                        idadeEditText.setText(Integer.toString(pessoa.getIdade()));
                        rootStorageRef.child("img").child(pessoa.getCpf()).getBytes(10 * 1024 * 1024).addOnSuccessListener(
                                new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        fotoImageView.setImageBitmap(bitmap);
                                    }
                                }
                        );
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void takePic (View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
        else{
            startActivityForResult( new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
        }

    }

    private final int REQUEST_CAMERA = 1;
    private final int REQUEST_PERMISSION_CAMERA = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            fotoImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startActivityForResult( new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
        }
    }
}
