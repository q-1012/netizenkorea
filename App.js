import { useRef, useEffect, useState } from 'react';
import { WebView } from 'react-native-webview';
import { StatusBar } from "expo-status-bar";
import { Alert, Platform, BackHandler, ToastAndroid } from "react-native";
import * as Notifications from "expo-notifications";
import * as Device from "expo-device";
import * as Linking from 'expo-linking';
import * as ScreenCapture from 'expo-screen-capture';
import { SafeAreaView, SafeAreaProvider } from "react-native-safe-area-context";
import { enableScreens } from 'react-native-screens';

enableScreens(false);

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

const toastWithDurationHandler = () => {
  ToastAndroid.show("'뒤로' 버튼을  한번 더 누르시면 종료됩니다.", ToastAndroid.SHORT);
};

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export default function App() {

  const defaultUri = 'https://m.nemobile.co.kr/account/signin';

  //푸시 관련
  const [expoPushToken, setExpoPushToken] = useState('');
  const [notification, setNotification] = useState(false);
  const notificationListener = useRef();
  const responseListener = useRef();
  const lastNotificationResponse = Notifications.useLastNotificationResponse();
  const [navUri, setNavUri] = useState('');

  //뒤로가기 처리
  const webview = useRef(null);
  const mainUrl = '/';
  let time = 0;
  const onAndroidBackPress = () => {
    if (!webview.current) {
      return;
    }

    if (mainUrl === '/') {
      time += 1;
        toastWithDurationHandler(); // 뒤로가기 토스트 바 
      if (time === 1) {
        setTimeout(() => time = 0, 2000);
      }
      else if (time === 2) {
        BackHandler.exitApp();
        return false;
      }
    } else {
      //webview.current.goBack();
      return true;
    }
    return true;
  };

  useEffect(() => {
    let isMounted = true;

    ScreenCapture.preventScreenCaptureAsync();

    //기본 uri 설정
    setNavUri('https://m.nemobile.co.kr/account/signin');

    //푸시
    registerForPushNotificationsAsync().then(token => setExpoPushToken(token));
    
    notificationListener.current = Notifications.addNotificationReceivedListener(notification => {
      setNotification(notification);
    });
    
    responseListener.current = Notifications.addNotificationResponseReceivedListener(response => {
      let link = response.notification.request.content.data.link;
      
      if(link) {
        //console.error('link before: ', link);
        setNavUri(link);
        console.error('navUri before: ', navUri);
        sleep(2000).then(() => {
          console.error('link after: ', link);
          //console.error('webview.current: ', webview.current);
          setNavUri(link);
          console.error('navUri after: ', navUri);

          if(webview.current) {
            webview.current.injectJavaScript('document.location.href="'+link+'";');
            console.error('js called: ', 'document.location.href="'+link+'";');
            
          } else {
            let enLink = encodeURIComponent(link);
            console.error('fetch called: ', 'https://m.nemobile.co.kr/common/pushLinkMove?link='+enLink+'&os='+Platform.OS);
            fetch('https://m.nemobile.co.kr/common/pushLinkMove?link='+enLink+'&os='+Platform.OS, {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              }
            }).catch((err) => console.error('err: ', err)); 
          }
        });
      }
    });

    if(lastNotificationResponse) {
      let link = lastNotificationResponse.notification.request.content.data.link;
      if(link) {
        //console.error('link before: ', link);
        setNavUri(link);
        console.error('navUri before: ', navUri);
        sleep(2000).then(() => {
          console.error('link after: ', link);
          //console.error('webview.current: ', webview.current);
          setNavUri(link);
          console.error('navUri after: ', navUri);

          if(webview.current) {
            webview.current.injectJavaScript('document.location.href="'+link+'";');
            console.error('js called: ', 'document.location.href="'+link+'";');
            
          } else {
            let enLink = encodeURIComponenet(link);
            console.error('fetch called: ', 'https://m.nemobile.co.kr/common/pushLinkMove?link='+enLink+'&os='+Platform.OS);
            fetch('https://m.nemobile.co.kr/common/pushLinkMove?link='+enLink+'&os='+Platform.OS, {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              }
            }).catch((err) => console.error('err: ', err)); 
          }
        });
      }
    }

    //카메라 권한
    //getCameraPermission();

    //뒤로가기
    BackHandler.addEventListener('hardwareBackPress', onAndroidBackPress);

    return () => {
      isMounted = false;
      Notifications.removeNotificationSubscription(notificationListener.current);
      Notifications.removeNotificationSubscription(responseListener.current);
      BackHandler.removeEventListener('hardwareBackPress', onAndroidBackPress);
    };
  }, []);

  const getCameraPermission = async () => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    //setHasPermission(status === 'granted');
  };

  async function registerForPushNotificationsAsync() {
    let token, rtv;
  
    if (Platform.OS === 'android') {
      await Notifications.setNotificationChannelAsync('default', {
        name: 'default',
        importance: Notifications.AndroidImportance.MAX,
        vibrationPattern: [0, 250, 250, 250],
        lightColor: '#FF231F7C',
      });
    }
  
    if (Device.isDevice) {
      const { status: existingStatus } = await Notifications.getPermissionsAsync();
      let finalStatus = existingStatus;
      if (existingStatus !== 'granted') {
        const { status } = await Notifications.requestPermissionsAsync();
        finalStatus = status;
      }
      if (finalStatus !== 'granted') {
        //console.log('Failed to get push token for push notification!');
        return;
      }
      // Learn more about projectId:
      // https://docs.expo.dev/push-notifications/push-notifications-setup/#configure-projectid
      token = (await Notifications.getExpoPushTokenAsync({ projectId: '700dd44c-9cb8-4de0-a3bd-fd443eb31f7b' }));
      if(token && token.data) {
        rtv=token.data;

        //console.log(rtv);

        fetch('https://m.nemobile.co.kr/common/putToken?token='+rtv+'&os='+Platform.OS, {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              }
            }).catch((err) => console.log('err: ', err));
      }
    } else {
      //console.log('Must use physical device for Push Notifications');
      return '';
    }
  
    return rtv;
  }

  const onMessage = (e) => {
  
    var param = JSON.parse(e.nativeEvent.data);
  
    console.log(param);

    switch(param.method) {
      default:
      break;
      case 'getFCMToken':
        registerForPushNotificationsAsync();
      break;
  
      case 'moveMarket':

      break;
      case 'showBrowser':
        Linking.openURL(param.data);
      break;
      case 'getVersion':

      break;
      case 'debugMsg':
        Alert.alert('data: ', param.data);
      break;
      case 'getLocation':
        
      break;
      case 'getAppPlatform' :
        if(webview.current) {
          const platformStr = Platform.OS;
          //console.log('platformStr: ', platformStr);
          webview.current.injectJavaScript('setAppPlatform("'+platformStr+'");');
        }
      break;
    }
  }

  return (
    <SafeAreaProvider>
      <SafeAreaView style={{ flex: 1, backgroundColor: "#ffffff" }}>
        <StatusBar style="dark" />
          <WebView
            ref={webview}
            source={{ uri: navUri }}
            onMessage={onMessage}
            setSupportMultipleWindows={false}
            javaScriptEnabled={true}
            domStorageEnabled={true}
            allowsFileAccess={true}
            allowFileAccessFromFileURLs={true}
            allowUniversalAccessFromFileURLs={true}
            style={{ flex: 1 }}
          />        
      </SafeAreaView>
    </SafeAreaProvider>
  );
}
