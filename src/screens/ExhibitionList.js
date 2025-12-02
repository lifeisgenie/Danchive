import React, { useEffect, useState } from 'react';
import { View, FlatList, Text, TouchableOpacity, StyleSheet, ActivityIndicator, Image } from 'react-native';
import axios from 'axios';
import { SafeAreaView } from 'react-native-safe-area-context';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';
const BASE = "http://100.84.161.55:8080/files/";

export default function ExhibitionList({ navigation }) {
    const [exhibitions, setExhibitions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchExhibitions = async () => {
            try {
                setLoading(true);
                setError('');
                const resp = await axios.get(`${API_BASE_URL}/exhibitions`);
                if (resp.data.success && Array.isArray(resp.data.data)) {
                    setExhibitions(resp.data.data);
                } else {
                    setExhibitions([]);
                }
            } catch (e) {
                setError('전시회 목록을 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchExhibitions();
    }, []);

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.itemWrap}
            onPress={() => navigation.navigate('ProjectList', { term: item.term })}
        >
            <Image
                source={{ uri: BASE + item.imageUrl }}
                style={styles.poster}
            />
            <View style={{ flex: 1 }}>
                <Text style={styles.itemTerm}>{item.term}</Text>
                <Text style={styles.itemTitle}>{item.title}</Text>
                <Text style={styles.itemPlace}>{item.place}</Text>
                <Text style={styles.itemDate}>{item.date}</Text>
            </View>
        </TouchableOpacity>
    );

    if (loading) return <ActivityIndicator style={{ margin: 40 }} size="large" color="#123" />;
    if (error) return <Text style={{ textAlign: 'center', margin: 30, color: 'red' }}>{error}</Text>;

    return (
        <SafeAreaView style={styles.container}>
            <View style={styles.logoWrapper}>
                <Image source={require('./assets/logo.png')} style={styles.logo} />
            </View>
            <FlatList
                data={exhibitions}
                renderItem={renderItem}
                keyExtractor={item => item.id.toString()}
                contentContainerStyle={{ padding: 14, paddingBottom: 30 }}
                ItemSeparatorComponent={() => <View style={{ height: 12 }} />}
                ListEmptyComponent={
                    <Text style={{ textAlign: 'center', color: '#888', marginTop: 60, fontWeight: 'bold' }}>
                        등록된 전시회가 없습니다.
                    </Text>
                }
                showsVerticalScrollIndicator={false}
            />
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    logoWrapper: { marginTop: 12, marginLeft: 16, marginBottom: 18 },
    logo: { width: 100, height: 25, marginTop: 10, resizeMode: 'contain' },
    itemWrap: {
        flexDirection: 'row',
        backgroundColor: '#fff',
        borderRadius: 16,
        padding: 18,
        alignItems: 'center',
        elevation: 1,
        marginBottom: 5,
        height: 160,
    },
    poster: {
        width: 100,
        height: 140,
        borderRadius: 8,
        marginRight: 16,
        backgroundColor: '#eee',
        resizeMode: 'cover',
    },
    itemTerm: { fontSize: 14, color: '#2c78b4', marginBottom: 4, fontWeight: 'bold' },
    itemTitle: { fontSize: 15, fontWeight: 'bold', marginBottom: 2 },
    itemPlace: { fontSize: 13, fontWeight: 'bold', color: '#666', marginBottom: 2 },
    itemDate: { fontSize: 13, fontWeight: 'bold', color: '#888' },
});
