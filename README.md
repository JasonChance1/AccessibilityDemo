1.创建AccessibilityService派生类MyService，并重写onAccessibilityEvent和onInterrupt方法
2.在清单文件中注册setvice:        <service android:name=".MyService"
            android:exported="false"
            android:label="点击器"
            android:enabled="true"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/assists_service" />
        </service>
3.在res/xml中创建assists_service.xml文件
