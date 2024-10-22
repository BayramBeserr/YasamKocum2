package com.example.yasamkocum2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String userName,otherName;
    TextView chatUserName;
    ImageView backImage, sendImage;
    EditText chatEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    RecyclerView chatRecyclerView;
    MesajAdapter mesajAdapter;
    List<MesajModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tanimla();
        loadMesaj();
    }

    public void tanimla(){

        list = new ArrayList<>();

        userName = getIntent().getExtras().getString("username");
        otherName = getIntent().getExtras().getString("othername");

        Log.i("alınandegerler : ",userName+"--"+otherName);
        chatUserName = (TextView)findViewById(R.id.chatUserName);
        backImage = (ImageView) findViewById(R.id.backImage);
        sendImage = (ImageView) findViewById(R.id.sendImage);
        chatEditText = (EditText) findViewById(R.id.chatEditText);
        chatUserName.setText(otherName);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                intent.putExtra("kadi",userName);
                startActivity(intent);
            }
        });
        firebaseDatabase = firebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaj = chatEditText.getText().toString();
                chatEditText.setText("");
                mesajGonder(mesaj);
            }
        });
        chatRecyclerView = (RecyclerView)findViewById(R.id.chatRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this,1);
        chatRecyclerView.setLayoutManager(layoutManager);
        mesajAdapter = new MesajAdapter(ChatActivity.this,list,ChatActivity.this,userName);
        chatRecyclerView.setAdapter(mesajAdapter);
    }


    public void mesajGonder(String text){
        final String key = reference.child("Mesajlar").child(userName).child(otherName).push().getKey();
        final Map messageMap = new HashMap();
        messageMap.put("text",text);
        messageMap.put("from",userName);
        reference.child("Mesajlar").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    reference.child("Mesajlar").child(otherName).child(userName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });
                }
            }
        });

    }

    public void loadMesaj(){
        reference.child("Mesajlar").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MesajModel mesajModel = dataSnapshot.getValue(MesajModel.class);
                list.add(mesajModel);
                mesajAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(list.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
