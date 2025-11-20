import React, { useState, useEffect } from 'react';
import { StatusBar, StyleSheet, useColorScheme, Text, View, ActivityIndicator } from 'react-native';
import Mainpage from './src/screens/Mainpage';
import MyPage from './src/screens/MyPage';
import NoticeDetail from './src/screens/Noticedetail';

//jw
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
  // 탭 or 아이콘 추가
  return (
    <Tab.Navigator screenOptions={{ headerShown: false }}>
      <Tab.Screen name="Main" component={Mainpage} />
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
            <Stack.Screen name="MainPage" component={Mainpage} />
            <Stack.Screen name="MyPage" component={MyPage} />
            <Stack.Screen name = "NoticeDetail" component={NoticeDetail} />
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
                <Text style={{marginTop: 10}}>인증 상태 확인 중...</Text>
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
        
        <Stack.Screen
          name="NoticeDetail"
          component={NoticeDetail}
          options={{ headerShown: true, title: '공지사항 상세' }}
        />
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