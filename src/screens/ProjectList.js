import React, { useEffect, useState } from 'react';
import {
    View, FlatList, Text, TouchableOpacity, StyleSheet, ActivityIndicator, TextInput,
    Modal, Pressable, Image
} from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

// award 등급에 따라 메달 이미지 반환 (이미지 파일을 프로젝트 폴더에 추가)
const awardMedal = {
    'GOLD': require('./assets/medal_gold.png'),
    'SILVER': require('./assets/medal_silver.png'),
    'BRONZE': require('./assets/medal_bronze.png'),
    'POPULAR': require('./assets/medal_popular.png'),
};

export default function ProjectList({ navigation, route }) {
    const currentTerm = route.params?.term || '';
    const [categories, setCategories] = useState(['전체']);
    const [loadingCat, setLoadingCat] = useState(true);

    const [filter, setFilter] = useState('전체');
    const [search, setSearch] = useState('');
    const [showFilter, setShowFilter] = useState(false);

    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const resp = await axios.get(`${API_BASE_URL}/categories`);
                if (resp.data?.success && Array.isArray(resp.data?.data)) {
                    setCategories(['전체', ...resp.data.data]);
                } else {
                    setCategories(['전체']);
                }
            } catch (err) {
                setCategories(['전체']);
            } finally {
                setLoadingCat(false);
            }
        };
        fetchCategories();
    }, []);

    useEffect(() => {
        const fetchProjects = async () => {
            setLoading(true);
            let params = [
                `term=${currentTerm}`,
                `page=${page}`,
                `size=15`,
                `sort=createdAt,desc`,
            ];
            if (filter !== '전체') params.push(`category=${filter}`);
            if (search.trim() !== '') params.push(`keyword=${search.trim()}`);
            const url = `${API_BASE_URL}/exhibits?${params.join('&')}`;
            try {
                const resp = await axios.get(url);
                if (resp.data?.success && resp.data?.data?.content) {
                    setProjects(page === 0 ? resp.data.data.content : [...projects, ...resp.data.data.content]);
                    setHasMore(resp.data.data.totalPages > page + 1);
                } else {
                    setProjects([]);
                    setHasMore(false);
                }
            } catch (err) {
                setProjects([]);
                setHasMore(false);
            } finally {
                setLoading(false);
            }
        };
        fetchProjects();
        // eslint-disable-next-line
    }, [currentTerm, filter, search, page]);

    useEffect(() => {
        setPage(0);
    }, [currentTerm, filter, search]);

    const handleLoadMore = () => {
        if (hasMore && !loading) setPage(prev => prev + 1);
    };

    // 팀원 리스트 예시 (팀원 정보는 API에 따라 teamMembers 등에서 받아야 함)
    const renderMembers = (item) => {
        // 만약 API에 members: [{name: xx}] 형태라면, 그에 맞게 처리
        if (Array.isArray(item.members)) {
            return item.members.map(m => m.name).join(', ');
        }
        return item.teamName || '';
    };

    // 수상작 메달 표시: awards 배열에 값 존재시 맨 첫번째 등급 기반
    const renderMedal = (item) => {
        if (item.awards && item.awards.length > 0) {
            const award = item.awards[0]; // 여러 개일 경우 첫번째만
            if (awardMedal[award]) {
                return <Image source={awardMedal[award]} style={styles.medalIcon} />;
            }
        }
        return null;
    };

    return (
        <View style={styles.container}>
            <View style={styles.searchRow}>
                <TextInput
                    style={styles.searchInput}
                    placeholder="이름, 학기, 작품명 등"
                    value={search}
                    onChangeText={setSearch}
                    returnKeyType="search"
                />
                <TouchableOpacity style={styles.filterBtn} onPress={() => setShowFilter(!showFilter)}>
                    <Image source={require('./assets/filter.png')} style={styles.filterIcon} />
                </TouchableOpacity>
                <Modal
                    transparent
                    animationType="fade"
                    visible={showFilter}
                    onRequestClose={() => setShowFilter(false)}
                >
                    <Pressable style={styles.modalBG} onPress={() => setShowFilter(false)}>
                        <View style={styles.filterModal}>
                            {loadingCat ? (
                                <ActivityIndicator size="small" color="#023560" />
                            ) : (
                                categories.map(cat => (
                                    <TouchableOpacity key={cat} style={styles.filterItem}
                                        onPress={() => { setFilter(cat); setShowFilter(false); }}>
                                        <Text style={{ color: filter === cat ? '#023560' : '#222' }}>{cat}</Text>
                                    </TouchableOpacity>
                                ))
                            )}
                        </View>
                    </Pressable>
                </Modal>
            </View>
            {loading && page === 0 && (
                <ActivityIndicator size="large" color="#023560" style={{ marginVertical: 30 }} />
            )}
            <FlatList
                data={projects}
                keyExtractor={item => String(item.id)}
                renderItem={({ item }) => (
                    <TouchableOpacity style={styles.itemCard}
                        onPress={() => navigation.navigate('ProjectDetail', { projectId: item.id })}
                    >
                        <Image
                            source={
                                item.thumbnailUrl
                                    ? { uri: `${API_BASE_URL}/${item.thumbnailUrl}` }
                                    : require('./assets/project-placeholder.png')
                            }
                            style={styles.itemImg}
                        />
                        <View style={{ flex: 1 }}>
                            <Text style={styles.itemTitle}>{item.title}</Text>
                            <View style={styles.itemRowBottom}>
                                <Text style={styles.itemMembers}>{renderMembers(item)}</Text>
                                {renderMedal(item)}
                            </View>
                        </View>
                    </TouchableOpacity>
                )}
                contentContainerStyle={{ paddingBottom: 30 }}
                ItemSeparatorComponent={() => <View style={{ height: 18 }} />}
                onEndReached={handleLoadMore}
                onEndReachedThreshold={0.9}
                ListEmptyComponent={() => !loading && (
                    <Text style={{ textAlign: 'center', marginVertical: 30, color: '#888' }}>작품이 없습니다.</Text>
                )}
                ListFooterComponent={() => loading && page > 0 && (
                    <ActivityIndicator size="small" color="#023560" style={{ margin: 18 }} />
                )}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff', padding: 10 },
    searchRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
    searchInput: {
        flex: 1,
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 9,
        paddingHorizontal: 12,
        fontSize: 15,
        paddingVertical: 7,
        backgroundColor: '#fafbfc'
    },
    filterBtn: { marginLeft: 8, padding: 8 },
    filterIcon: { width: 19, height: 19 },
    modalBG: { flex: 1, backgroundColor: 'rgba(0,0,0,0.14)', justifyContent: 'flex-start', alignItems: 'flex-end' },
    filterModal: {
        marginTop: 58,
        marginRight: 18,
        borderRadius: 13,
        backgroundColor: '#fff',
        padding: 13,
        width: 130,
        elevation: 6,
        shadowColor: '#000', shadowOpacity: 0.11, shadowOffset: { width: 0, height: 10 }
    },
    filterItem: { paddingVertical: 11, paddingHorizontal: 6 },
    itemCard: { flexDirection: 'row', padding: 9, backgroundColor: '#fafdff', borderRadius: 14, alignItems: 'center', minHeight: 84 },
    itemImg: { width: 82, height: 82, borderRadius: 12, marginRight: 14, backgroundColor: '#ececec' },
    itemTitle: { fontSize: 15, fontWeight: 'bold', marginBottom: 8 },
    itemRowBottom: { flexDirection: 'row', alignItems: 'center', marginTop: 2 },
    itemMembers: { fontSize: 13, color: '#444', flexShrink: 1, marginRight: 8 },
    medalIcon: { width: 18, height: 18, resizeMode: 'contain' },
});

