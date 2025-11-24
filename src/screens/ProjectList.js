import React, { useEffect, useState } from 'react';
import { View, FlatList, Text, Image, StyleSheet, ActivityIndicator } from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

export default function ProjectList({ route }) {
    const { term } = route.params; // ExhibitionList에서 넘어온 학기(term)
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                setLoading(true);
                setError('');
                const resp = await axios.get(`${API_BASE_URL}/exhibits?term=${term}`);
                console.log('작품 목록 API 응답:', resp.data);
                if (resp.data.success && resp.data.data?.content?.length) {
                    setProjects(resp.data.data.content);
                } else {
                    setProjects([]);
                }
            } catch (e) {
                setError('작품 목록을 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchProjects();
    }, [term]);

    const renderItem = ({ item }) => (
        <View style={styles.card}>
            <Image
                source={{ uri: `${API_BASE_URL.replace('/api/v1', '')}/${item.thumbnailUrl}` }}
                style={styles.thumbnail}
            />
            <View style={{ flex: 1 }}>
                <Text style={styles.title}>{item.title}</Text>
                <Text style={styles.team}>{item.teamName}</Text>
                <Text style={styles.short}>{item.shortIntro}</Text>
                <Text style={styles.categories}>{item.categories.join(', ')}</Text>
            </View>
        </View>
    );

    if (loading) return <ActivityIndicator style={{ margin: 40 }} size="large" color="#123" />;
    if (error) return <Text style={{ textAlign: 'center', color: 'red', margin: 30 }}>{error}</Text>;

    return (
        <View style={styles.container}>
            <FlatList
                data={projects}
                renderItem={renderItem}
                keyExtractor={item => item.id.toString()}
                contentContainerStyle={{ padding: 16, paddingBottom: 30 }}
                ItemSeparatorComponent={() => <View style={{ height: 16 }} />}
                ListEmptyComponent={<Text style={{ textAlign: 'center', color: '#888', marginTop: 20 }}>등록된 작품이 없습니다.</Text>}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    card: {
        flexDirection: 'row',
        backgroundColor: '#fafdff',
        borderRadius: 14,
        padding: 12,
        alignItems: 'flex-start',
        elevation: 1,
    },
    thumbnail: { width: 60, height: 80, borderRadius: 8, marginRight: 12, backgroundColor: '#eee' },
    title: { fontSize: 15, fontWeight: 'bold', marginBottom: 4 },
    team: { fontSize: 13, color: '#2c78b4', marginBottom: 2 },
    short: { fontSize: 12, color: '#555', marginBottom: 2 },
    categories: { fontSize: 12, color: '#888' },
});
