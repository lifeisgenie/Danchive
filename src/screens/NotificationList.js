import React, { useEffect, useState } from 'react';
import { FlatList, Text, View, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';

const NOTIFICATION_KEY = 'notificationList';

export default function NotificationListScreen( {navigation} ) {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const fetchNotifications = async () => {
            const listStr = await AsyncStorage.getItem(NOTIFICATION_KEY);
            if (listStr) setNotifications(JSON.parse(listStr));
        };
        fetchNotifications();

        // 화면 돌아올 때마다 최신 목록 로드 (포커스 시 리로드 등도 구현 가능)
        const focusListener = navigation.addListener('focus', fetchNotifications);
        return focusListener;
    }, []);

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <Text style={styles.header}>알림 목록</Text>
            {notifications.length === 0 ? (
                <View style={styles.empty}><Text>알림이 없습니다.</Text></View>
            ) : (
                <FlatList
                    data={notifications}
                    keyExtractor={item => String(item.id)}
                    renderItem={({ item }) => (
                        <View style={styles.item}>
                            <Text style={styles.title}>{item.title}</Text>
                            <Text style={styles.body}>{item.body}</Text>
                            <Text style={styles.date}>{item.receivedAt?.slice(0, 10)}</Text>
                        </View>
                    )}
                />
            )}
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    header: { fontSize: 19, fontWeight: '700', margin: 20 },
    empty: { alignItems: 'center', marginTop: 30 },
    item: { padding: 16, borderBottomWidth: 1, borderColor: '#eee' },
    title: { fontWeight: 'bold', fontSize: 16, marginBottom: 2 },
    body: { fontSize: 14, color: '#444' },
    date: { fontSize: 11, color: '#bbb', marginTop: 2, alignSelf: 'flex-end' },
});
