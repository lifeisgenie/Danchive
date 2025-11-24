import React, { useEffect, useState } from 'react';
import { View, FlatList, Text, TouchableOpacity, StyleSheet, ActivityIndicator } from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1'; // 실제 엔드포인트로 변경

export default function ExhibitionList({ navigation }) {
    const [terms, setTerms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchTerms = async () => {
            try {
                setLoading(true);
                setError('');
                const resp = await axios.get(`${API_BASE_URL}/exhibits/terms`);
                // 서버 응답 예시: { success: true, message: "", data: {currentTerm, terms: [...]}}
                if (resp.data.success && resp.data.data.terms?.length) {
                    setTerms(resp.data.data.terms);
                } else {
                    setTerms([]);
                }
            } catch (e) {
                setError('학기 목록을 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchTerms();
    }, []);

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.itemWrap}
            onPress={() =>
                navigation.navigate('Projectlist', { term: item })
            }
        >
            <View style={{ flex: 1, justifyContent: 'center' }}>
                <Text style={styles.itemTitle}>{item}</Text>
                {/* 여기에 실제로 학기명을 더 포맷팅하거나, api에서 title, desc, image 정보에 맞춰 커스텀 */}
            </View>
        </TouchableOpacity>
    );

    if (loading) return <ActivityIndicator style={{ margin: 40 }} size="large" color="#123" />;
    if (error) return <Text style={{ textAlign: 'center', margin: 30, color: 'red' }}>{error}</Text>;

    return (
        <View style={styles.container}>
            <FlatList
                data={terms}
                renderItem={renderItem}
                keyExtractor={item => item}
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
        marginBottom: 5
    },
    itemTitle: { fontSize: 15, fontWeight: 'bold' },
});
