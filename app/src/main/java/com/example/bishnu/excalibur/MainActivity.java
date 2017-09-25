package com.example.bishnu.excalibur;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.yalantis.flipviewpager.utils.FlipSettings;


public class MainActivity extends AppCompatActivity {
    private String[] permissionRequiredList = new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET};
    private static final int REQUEST_CODE_PERMISSION = 25;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private Button addButton;
    private Button cancelButton;
    private EditText nameEV;
    private EditText employeeIdEV;
    private EditText mobileNumberEV;
    private EditText imageUrlEV;
    private EditText skillsEV;
    private ListView employeeListView;
    private PaginatedScanList<Employee> employeeList;
    private FlipSettings settings;
    private DynamoDBScanExpression scanExpression;
    private EmployeeListAdapter employeeListApater;
    private AlertDialog alertDialog;
    private Menu menu;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!requestPermissions()) {
            // Initialize the Amazon Cognito credentials provider
            initCredentialProvider();
        }
        initView();
        initListner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_employee_details_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_employee_details: {
                if (alertDialog == null || !alertDialog.isShowing()) {
                    creatandShowDialogView(true);
                } else {
                    hideDialogView(true);
                }
                item.setIcon(getDrawable(alertDialog != null && alertDialog.isShowing() ? R.drawable.cross_icon : R.drawable.plus_icon));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void creatandShowDialogView(boolean isAddEmployee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_layout, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        initDialogView(dialogView);
        if (isAddEmployee) {
            addButton.setText("ADD");
            deleteBtn.setVisibility(View.INVISIBLE);
        } else {
            addButton.setText("Update");
            deleteBtn.setVisibility(View.VISIBLE);
        }
        initAddEmployeedetailsDialogListner(isAddEmployee);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /*private void initEditEmployeeDetailsDialogLisnter() {
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialogView(false);
                }
            });
        }
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        final Employee employee = new Employee(nameEV.getText().toString()
                                , employeeIdEV.getText().toString()
                                , imageUrlEV.getText().toString()
                                , mobileNumberEV.getText().toString()
                                , skillsEV.getText().toString());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (employee != null) {
                                    mapper.save(employee);
                                    employeeList = mapper.scan(Employee.class, scanExpression);
                                    employeeListApater = new EmployeeListAdapter(getApplicationContext(), employeeList, settings);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            employeeListView.setAdapter(employeeListApater);
                                        }
                                    });
                                }
                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
                        hideDialogView(false);
                    }catch (Exception e)
                    {
                        Log.d("EditButton", "onClick: "+ e.getMessage());
                    }
                }
            });
        }
    }*/

    private void initAddEmployeedetailsDialogListner(final boolean isAddEmployee) {
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addOrDeleteEmployee(true);
                }
            });
        }
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialogView(isAddEmployee);
                }
            });
        }
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addOrDeleteEmployee(false);
                }
            });
        }
    }

    private void addOrDeleteEmployee(final boolean isAddEmployee) {
        final Employee employee = new Employee(nameEV.getText().toString()
                , employeeIdEV.getText().toString()
                , imageUrlEV.getText().toString()
                , mobileNumberEV.getText().toString()
                , skillsEV.getText().toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (isAddEmployee) {
                        mapper.save(employee);
                    } else {
                        mapper.delete(employee);
                    }
                    employeeList = mapper.scan(Employee.class, scanExpression);
                    employeeListApater = new EmployeeListAdapter(getApplicationContext(), employeeList, settings);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            employeeListView.setAdapter(employeeListApater);
                        }
                    });
                } catch (Exception e) {
                    Log.d("Exception", "run: " + e.getMessage());
                }
            }
        };
        Thread writeThread = new Thread(runnable);
        writeThread.start();
        hideDialogView(isAddEmployee);
    }

    private void initDialogView(View dialogView) {
        addButton = (Button) dialogView.findViewById(R.id.add_btn);
        cancelButton = (Button) dialogView.findViewById(R.id.cancel_btn);
        nameEV = (EditText) dialogView.findViewById(R.id.name_edit_View);
        mobileNumberEV = (EditText) dialogView.findViewById(R.id.mobile_number_edit_View);
        skillsEV = (EditText) dialogView.findViewById(R.id.skills_edit_View);
        imageUrlEV = (EditText) dialogView.findViewById(R.id.image_url_edit_View);
        employeeIdEV = (EditText) dialogView.findViewById(R.id.id_edit_View);
        deleteBtn = (Button) dialogView.findViewById(R.id.delete_btn);
    }


    public void hideDialogView(boolean isAddEmployee) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (isAddEmployee) {
            MenuItem menuItem = menu.getItem(0);
            if (menuItem != null) {
                menuItem.setIcon(R.drawable.plus_icon);
            }

        }
    }


    private void initListner() {
        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Employee employee = (Employee) employeeListView.getAdapter().getItem(i);
                creatandShowDialogView(false);
                nameEV.setText(employee.getName());
                imageUrlEV.setText(employee.getImage());
                skillsEV.setText(employee.getSkills());
                mobileNumberEV.setText(employee.getMobileNumber());
                employeeIdEV.setText(employee.getId());
                employeeIdEV.setEnabled(false);
            }
        });
    }

    private void initView() {
        employeeListView = (ListView) findViewById(R.id.employee_listview);
        settings = new FlipSettings.Builder().defaultPage(1).build();
        scanExpression = new DynamoDBScanExpression();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                employeeList = mapper.scan(Employee.class, scanExpression);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        employeeListApater = new EmployeeListAdapter(getApplicationContext(), employeeList, settings);
                        employeeListView.setAdapter(employeeListApater);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private boolean requestPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permissionRequiredList[0]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),
                permissionRequiredList[1]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionRequiredList, REQUEST_CODE_PERMISSION);
            return true;
        } else {
            return false;
        }
    }

    private void initCredentialProvider() {
        if (ddbClient == null) {
            ddbClient = AmazonDynamoDBAdapter.getAmazonDynamoDBClient(getApplicationContext());
        }
        if (mapper == null) {
            mapper = new DynamoDBMapper(ddbClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = true;
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    showToastIfPermissionIsNotGranted(permissionRequiredList[i]);
                }
            }
            if (allGranted) {
                initCredentialProvider();
            } else {
                this.finish();
            }
        }
    }

    private void showToastIfPermissionIsNotGranted(String requestedPermission) {
        Toast.makeText(this, new StringBuilder("Please accept the " + requestedPermission + "permission or application will be closed")
                , Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
