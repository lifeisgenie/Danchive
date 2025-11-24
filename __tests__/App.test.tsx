/**
 * @format
 */

import 'react-native';
import React from 'react';
import renderer, { act } from 'react-test-renderer';

const errorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
afterAll(() => errorSpy.mockRestore());

// React Navigation (native) mock
jest.mock('@react-navigation/native', () => ({
  NavigationContainer: ({ children }: any) => children,
  useNavigation: () => ({
    navigate: jest.fn(),
  }),
}));

// Bottom Tabs mock
jest.mock('@react-navigation/bottom-tabs', () => ({
  createBottomTabNavigator: () => ({
    Navigator: ({ children }: any) => children,
    Screen: ({ children }: any) => children,
  }),
}));

// Stack Navigator mock
jest.mock('@react-navigation/stack', () => ({
  createStackNavigator: () => ({
    Navigator: ({ children }: any) => children,
    Screen: ({ children }: any) => children,
  }),
}));

// SafeArea mock
jest.mock('react-native-safe-area-context', () => ({
  SafeAreaProvider: ({ children }: any) => children,
  SafeAreaView: ({ children }: any) => children,
  useSafeAreaInsets: () => ({ top: 0, bottom: 0, left: 0, right: 0 }),
}));

// AsyncStorage mock
jest.mock('@react-native-async-storage/async-storage', () => {
  const asMock = require('@react-native-async-storage/async-storage/jest/async-storage-mock');
  return asMock;
});

// Ionicons mock
jest.mock('react-native-vector-icons/Ionicons', () => 'Ionicons');

import App from '../App';

it('renders correctly', async () => {
  await act(async () => {
    renderer.create(<App />);
  });
});