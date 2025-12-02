import React, { useEffect, useState } from 'react';
import { View, FlatList, Text, Image, StyleSheet, ActivityIndicator, TextInput, TouchableOpacity } from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

export default function ProjectList({ route, navigation }) {
    const { term } = route.params;
    const [projects, setProjects] = useState([]);
    const [categories, setCategories] = useState(['ALL']);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [search, setSearch] = useState('');
    const [filterVisible, setFilterVisible] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState('ALL');

    // Category 목록 API로 불러옴
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const resp = await axios.get(`${API_BASE_URL}/categories`);
                if (resp.data.success && Array.isArray(resp.data.data)) {
                    setCategories(['ALL', ...resp.data.data]);
                }
            } catch (e) {
                setCategories(['ALL']);
            }
        };
        fetchCategories();
    }, []);

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                setLoading(true);
                setError('');
                const resp = await axios.get(`${API_BASE_URL}/exhibits?term=${term}`);
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

    const filteredProjects = projects.filter(project =>
        (selectedCategory === 'ALL' || project.categories.includes(selectedCategory)) &&
        (!search || project.title.includes(search) || project.teamName.includes(search))
    );

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.card}
            onPress={() => navigation.navigate('ProjectPage', { projectId: item.id })}
            activeOpacity={0.85}
        >
            <Image
                source={{ uri: `${API_BASE_URL.replace('/api/v1', '/files')}/${item.thumbnailUrl}` }}
                style={styles.thumbnail}
            />
            <View style={{ flex: 1 }}>
                <Text style={styles.title}>{item.title}</Text>
                <Text style={styles.team}>{item.teamName}</Text>
                <Text style={styles.short}>{item.shortIntro}</Text>
                <Text style={styles.categories}>{item.categories.join(', ')}</Text>
            </View>
        </TouchableOpacity>
    );

    return (
        <View style={{ flex: 1, backgroundColor: '#fff' }}>
            {/* 상단 로고 + 검색바 */}
            <View style={styles.logoWrap}>
                <Image source={require('./assets/logo.png')} style={styles.logo} />
            </View>
            <View style={styles.searchWrap}>
                <TextInput
                    style={styles.searchInput}
                    placeholder="이름, 학기, 팀명 등"
                    value={search}
                    onChangeText={setSearch}
                />
                <TouchableOpacity style={styles.filterBtn} onPress={() => setFilterVisible(v => !v)}>
                    <Text style={styles.filterText}>⌵</Text>
                </TouchableOpacity>
            </View>
            {/* 필터 드롭다운을 화면 전체에 절대 위치로 분리 */}
            {filterVisible && (
                <View style={styles.filterDropdown}>
                    {categories.map(cat => (
                        <TouchableOpacity key={cat} onPress={() => { setSelectedCategory(cat); setFilterVisible(false); }}>
                            <Text style={[
                                styles.filterItem,
                                selectedCategory === cat && styles.filterItemActive
                            ]}>{cat}</Text>
                        </TouchableOpacity>
                    ))}
                </View>
            )}

            {/* 리스트 */}
            <FlatList
                data={filteredProjects}
                renderItem={renderItem}
                keyExtractor={item => item.id.toString()}
                contentContainerStyle={{ padding: 16, paddingBottom: 30 }}
                ItemSeparatorComponent={() => <View style={{ height: 16 }} />}
                ListEmptyComponent={
                    <Text style={{ textAlign: 'center', color: '#888', marginTop: 20 }}>등록된 작품이 없습니다.</Text>
                }
                showsVerticalScrollIndicator={false}
            />
        </View>
    );


}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    logoWrap: { marginTop: 30, marginLeft: 16, marginBottom: 18 },
    logo: { width: 100, height: 25, marginTop: 18, resizeMode: 'contain' },
    searchWrap: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 8,
        marginLeft: 14,
        marginRight: 14,
        position: 'relative'
    },
    searchInput: {
        flex: 1,
        width: '100%',
        borderRadius: 19,
        backgroundColor: '#f5f7fa',
        fontSize: 15,
        paddingHorizontal: 18,
        fontWeight: 'bold',
        height: 40,
        borderWidth: 0,
    },
    filterBtn: {
        backgroundColor: '#eaeaea',
        borderRadius: 10,
        paddingHorizontal: 10,
        paddingVertical: 7,
        marginLeft: 9,
    },
    filterText: {
        fontWeight: 'bold',
        fontSize: 18,
        color: '#789',
    },
    filterDropdown: {
        position: 'absolute',
        top: 66, // 로고 + 검색바 높이만큼 아래에 위치
        right: 20,
        backgroundColor: '#ececec',
        borderRadius: 8,
        padding: 8,
        elevation: 5,
        zIndex: 9999, // 최상단!
        minWidth: 80,
    },

    filterList: {
        position: 'absolute',
        top: 38,
        right: 0,
        backgroundColor: '#ececec',
        borderRadius: 8,
        padding: 8,
        elevation: 5,
        zIndex: 99,
        minWidth: 70,
    },
    filterItem: {
        fontSize: 14,
        paddingVertical: 8,
        color: '#333',
        fontWeight: 'bold',
    },
    filterItemActive: {
        color: '#295cae',
        backgroundColor: '#dee5f9',
        borderRadius: 6,
        paddingHorizontal: 6,
    },
    card: {
        flexDirection: 'row',
        backgroundColor: '#fafdff',
        borderRadius: 14,
        padding: 12,
        alignItems: 'flex-start',
        elevation: 1,
    },
    thumbnail: { width: 72, height: 72, borderRadius: 11, marginRight: 12, backgroundColor: '#eee' },
    title: { fontSize: 15, fontWeight: 'bold', marginBottom: 2 },
    team: { fontSize: 13, color: '#2c78b4', marginBottom: 2, fontWeight: 'bold' },
    short: { fontSize: 12, color: '#555', marginBottom: 2 },
    categories: { fontSize: 12, color: '#888' },
});
