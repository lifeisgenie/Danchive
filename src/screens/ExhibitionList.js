import React, { useEffect, useState } from 'react';
import { View, FlatList, Text, TouchableOpacity, StyleSheet, ActivityIndicator } from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1'; // 실제 엔드포인트로 변경

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
                console.log('API 응답:', resp.data);
                if (resp.data.success && Array.isArray(resp.data.data)) {
                    setExhibitions(resp.data.data);
                } else {
                    setExhibitions([]);
                }
            } catch (e) {
                setError('전시회 목록을 불러오는 데 실패했습니다.');
                console.log('API 호출 에러:', e);
            } finally {
                setLoading(false);
            }
        };
        fetchExhibitions();
    }, []);

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.itemWrap}
            onPress={() =>
                navigation.navigate('ProjectList', { term: item.term })
            }
        >
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
        <View style={styles.container}>
            <FlatList
                data={exhibitions}
                renderItem={renderItem}
                keyExtractor={item => item.id.toString()}
                contentContainerStyle={{ padding: 14, paddingBottom: 30 }}
                ItemSeparatorComponent={() => <View style={{ height: 12 }} />}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    itemWrap: {
        flexDirection: 'row',
        backgroundColor: '#fafdff',
        borderRadius: 16,
        padding: 18,
        alignItems: 'center',
        elevation: 1,
        marginBottom: 5,
    },
    itemTerm: { fontSize: 14, color: '#2c78b4', marginBottom: 4 },
    itemTitle: { fontSize: 15, fontWeight: 'bold', marginBottom: 2 },
    itemPlace: { fontSize: 13, color: '#666', marginBottom: 2 },
    itemDate: { fontSize: 13, color: '#888' },
});

