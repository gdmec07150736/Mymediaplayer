package com.example.administrator.mymediaplayer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Vector;

public class Main2Activity extends Activity{
    private final String[] filemap={".3gp",".mov",".rmvb",".wmv",".mp3",".mp4",".avi"};
    private Vector<String> items=null;
    private Vector<String> paths=null;
    private Vector<String> sizes=null;
    private String root="/mut/sdcard";
    private EditText pathed;
    private Button queryb;
    private ListView filelv;

    private void fileordir(String path){
        File f=new File(path);
        if(f.isDirectory()){
            getfilesdir(f.getPath());
        }else{
            openfile(path);
        }
    }
    private void getfilesdir(String path){
        pathed.setText(path);
        items=new Vector<String>();
        paths=new Vector<String>();
        sizes=new Vector<String>();
        File f=new File(path);
        File[] fs=f.listFiles();
        if(fs!=null){
            for(int i=0;i<fs.length;i++){
                if(fs[i].isDirectory()){
                    items.add(fs[i].getName());
                    paths.add(fs[i].getPath());
                    sizes.add("");
                }
            }
            for(int i=0;i<fs.length;i++){
                if(fs[i].isFile()){
                    String filename=fs[i].getName();
                    int index=filename.lastIndexOf(".");
                    if(index>0){
                        String endname=filename.substring(index,filename.length()).toLowerCase();
                        String type=null;
                        for(int x=0;x<filemap.length;x++){
                            if(endname.equals(filemap[x])){
                                type=filemap[x];
                                break;
                            }
                        }
                        if(type!=null){
                            items.add(fs[i].getName());
                            paths.add(fs[i].getPath());
                            sizes.add(fs[i].length()+"");
                        }
                    }
                }
            }
        }
        filelv.setAdapter(new FileListAdapter(this,items));
    }
    public void openfile(String path){
        Intent in=new Intent(Main2Activity.this,MainActivity.class);
        in.putExtra("path",path);
        startActivity(in);
        finish();
    }
    class FileListAdapter extends BaseAdapter{
        private Vector<String> items=null;
        private Main2Activity myfile;
        public FileListAdapter(Main2Activity myfile,Vector<String> items){
            this.items=items;
            this.myfile=myfile;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.elementAt(position);
        }

        @Override
        public long getItemId(int position) {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=myfile.getLayoutInflater().inflate(R.layout.file_item,null);
            }
            TextView name=(TextView) convertView.findViewById(R.id.name);
            ImageView music=(ImageView) convertView.findViewById(R.id.music);
            ImageView folder=(ImageView) convertView.findViewById(R.id.folder);
            name.setText(items.elementAt(position));
            if(sizes.elementAt(position).equals("")){
                music.setVisibility(View.GONE);
                folder.setVisibility(View.VISIBLE);
            }else{
                folder.setVisibility(View.GONE);
                music.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("浏览文件");
        setContentView(R.layout.myfile);
        pathed=(EditText) findViewById(R.id.pathed);
        queryb=(Button) findViewById(R.id.qryb);
        filelv=(ListView) findViewById(R.id.filelv);
        queryb.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f=new File(pathed.getText().toString());
                if(f.exists()){
                    if(f.isFile()){
                        openfile(pathed.getText().toString());
                    }else{
                        getfilesdir(pathed.getText().toString());
                    }
                }else{
                    Toast.makeText(Main2Activity.this, "未找到位置", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileordir(paths.get(position));
            }
        });
        getfilesdir(root);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            pathed=(EditText) findViewById(R.id.pathed);
            File f=new File(pathed.getText().toString());
            if(root.equals(pathed.getText().toString().trim())){
                return super.onKeyDown(keyCode,event);
            }else{
                getfilesdir(f.getParent());
                return true;
            }
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
