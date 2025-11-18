import React from 'react';
import { StatusBar, StyleSheet, useColorScheme } from 'react-native';
import Mainpage from './src/screens/Mainpage';
import MyPage from './src/screens/MyPage';
import NoticeDetail from './src/screens/Noticedetail';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

function TabNavigator() {
  return (
    <Tab.Navigator screenOptions={{ headerShown: false }}>
      <Tab.Screen name="Main" component={Mainpage} />
      <Tab.Screen name="MyPage" component={MyPage} />
    </Tab.Navigator>
  );
}

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Tabs" screenOptions={{ headerShown: false }}>
          <Stack.Screen name="Tabs" component={TabNavigator} />
          <Stack.Screen
            name="NoticeDetail"
            component={NoticeDetail}
            options={{ headerShown: true, title: '공지사항 상세' }}
          />
        </Stack.Navigator>
      </NavigationContainer>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default App;
