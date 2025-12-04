import React, { useState, useEffect } from 'react';
import { StatusBar, StyleSheet, useColorScheme, Text, View, ActivityIndicator, Image } from 'react-native';
import MainPage from './src/screens/MainPage';
import MyPage from './src/screens/MyPage';
import NoticeDetail from './src/screens/NoticeDetail';
import NotificationList from './src/screens/NotificationList';
import ProjectList from './src/screens/ProjectList';
import ExhibitionList from './src/screens/ExhibitionList';
import ProjectPage from './src/screens/ProjectPage';
import ProjectRegister from './src/screens/ProjectRegister';
import ProfEvaluation from './src/screens/evaluation/ProfEvaluation';
import UserEvaluation from './src/screens/evaluation/UserEvaluation';

import LandingScreen from './src/screens/Auth/index';
import SignInPage from './src/screens/Auth/signin';
import SignUpPage from './src/screens/Auth/signup';

import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

function TabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarShowLabel: false, // 글자 없애기
        tabBarStyle: {
          backgroundColor: 'white', // 배경
          borderTopWidth: 0,
          elevation: 0,
          position: 'absolute', // 필요하면 아래 고정
        },
        tabBarIcon: ({ focused }) => {
          let icon;
          if (route.name === 'Main') {
            icon = require('./src/screens/assets/home.png');
          } else if (route.name === 'Exhibitions') {
            icon = require('./src/screens/assets/exhibitions.png');
          } else if (route.name === 'MyPage') {
            icon = require('./src/screens/assets/mypage.png');
          }
          return (
            <Image
              source={icon}
              style={{
                width: 28,
                height: 28,
                opacity: focused ? 1 : 0.5, // 선택시 밝게, 미선택시 반투명
              }}
              resizeMode="contain"
            />
          );
        }
      })}
    >
      <Tab.Screen name="Main" component={MainPage} />
      <Tab.Screen name="Exhibitions" component={ExhibitionList} />
      <Tab.Screen name="MyPage" component={MyPage} />
    </Tab.Navigator>
  );
}

function AuthStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Landing" component={LandingScreen} />
      <Stack.Screen name="SignIn" component={SignInPage} />
      <Stack.Screen name="SignUp" component={SignUpPage} />
      <Stack.Screen name="MainPage" component={MainPage} />
      <Stack.Screen name="MyPage" component={MyPage} />
      <Stack.Screen name="NoticeDetail" component={NoticeDetail} />
      <Stack.Screen name="NotificationList" component={NotificationList} />
      <Stack.Screen name="ProjectList" component={ProjectList} />
      <Stack.Screen name="ExhibitionList" component={ExhibitionList} />
      <Stack.Screen name="ProjectPage" component={ProjectPage} />
      <Stack.Screen name="ProjectRegister" component={ProjectRegister} />
      <Stack.Screen name="ProfEvaluation" component={ProfEvaluation} />
      <Stack.Screen name="UserEvaluation" component={UserEvaluation} />
    </Stack.Navigator>
  );
}

function RootNavigator() {
  const [isLoading, setIsLoading] = useState(true);
  const [userToken, setUserToken] = useState(null);

  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const token = await AsyncStorage.getItem('userToken');
        setUserToken(token);
      } catch (e) {
        console.error("AsyncStorage read error:", e);
      } finally {
        setIsLoading(false);
      }
    };
    checkAuthStatus();
  }, []);

  if (isLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#0000ff" />
        <Text style={{ marginTop: 10 }}>인증 상태 확인 중...</Text>
      </View>
    );
  }

  return (
    <Stack.Navigator
      initialRouteName={userToken ? "Tabs" : "Auth"}
      screenOptions={{ headerShown: false }}
    >
      <Stack.Screen name="Auth" component={AuthStack} />
      <Stack.Screen name="Tabs" component={TabNavigator} />
      <Stack.Screen name="NoticeDetail" component={NoticeDetail} options={{ headerShown: true, title: '공지사항 상세' }} />
      <Stack.Screen name="NotificationList" component={NotificationList} options={{ headerShown: true, title: '알림 목록' }} />
      <Stack.Screen name="ProjectList" component={ProjectList}/>
      <Stack.Screen name="ExhibitionList" component={ExhibitionList} />
      <Stack.Screen name="ProjectPage" component={ProjectPage} />
      <Stack.Screen name="ProjectRegister" component={ProjectRegister} options={{ headerShown: true, title: '작품 등록' }} />
      <Stack.Screen name="ProfEvaluation" component={ProfEvaluation} />
      <Stack.Screen name="UserEvaluation" component={UserEvaluation} />
    </Stack.Navigator>
  );
}

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default App;
