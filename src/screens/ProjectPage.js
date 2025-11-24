import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, Image, ScrollView, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

// 메달 아이콘 사전 준비
const MEDAL_ICON = {
    GOLD: require('./assets/medal_gold.png'),
    SILVER: require('./assets/medal_silver.png'),
    BRONZE: require('./assets/medal_bronze.png'),
    POPULAR: require('./assets/medal_popular.png'),
};

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

export default function ProjectDetail({ route, navigation }) {
    const { projectId } = route.params;
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [userRole, setUserRole] = useState('');

    useEffect(() => {
        // 토큰/Role 가져오기 (예: AsyncStorage 등)
        const fetchRole = async () => {
            const role = await AsyncStorage.getItem('userRole'); // 로그인 시 저장했다고 가정
            setUserRole(role || '');
        };
        fetchRole();

        // 작품 상세 데이터 요청
        const fetchDetail = async () => {
            setLoading(true);
            try {
                const resp = await axios.get(`${API_BASE_URL}/exhibits/${projectId}`);
                if (resp.data?.success) {
                    setDetail(resp.data.data);
                } else {
                    Alert.alert('오류', resp.data.message || '작품 상세를 불러올 수 없습니다.');
                }
            } catch (e) {
                Alert.alert('서버 오류', e.response?.data?.message || '네트워크 오류');
            }
            setLoading(false);
        };
        fetchDetail();
    }, [projectId]);

    if (loading || !detail) {
        return <ActivityIndicator style={{ margin: 44 }} size="large" color="#123" />;
    }

    // 수상 여부
    const award = Array.isArray(detail.awards) && detail.awards.length > 0 ? detail.awards[0] : null;

    // 팀원 표시 (API에 따라 members = 배열 or teamName/array)
    const teamMembers = Array.isArray(detail.members)
        ? detail.members.map(m => m.name).join(', ')
        : (detail.teamMembers || detail.teamName || '');

    return (
        <ScrollView style={styles.container}>
            {/* 작품 포스터 */}
            <Image
                source={detail.posterUrl
                    ? { uri: `${API_BASE_URL}/${detail.posterUrl}` }
                    : require('./assets/project-placeholder.png')}
                style={styles.posterImg}
            />
            {/* 제목, 수상, 팀원 */}
            <View style={styles.infoBox}>
                <View style={styles.titleRow}>
                    <Text style={styles.projectTitle}>{detail.title}</Text>
                    {!!award && MEDAL_ICON[award] && (
                        <Image source={MEDAL_ICON[award]} style={styles.medalIcon} />
                    )}
                </View>
                <View style={styles.subInfoRow}>
                    <Text style={styles.metaText}>
                        {detail.categories?.join(', ')}
                        {detail.categories?.length && teamMembers ? ' | ' : ''}
                        {teamMembers}
                    </Text>
                </View>
            </View>
            {/* 상세 설명 */}
            <Text style={styles.detailDesc}>{detail.intro}</Text>

            {/* 하단 평가 버튼 */}
            <TouchableOpacity
                style={styles.evalBtn}
                onPress={() => {
                    if (userRole === 'prof') {
                        navigation.navigate('ProjectEvalProfessor', { projectId });
                    } else if (userRole === 'team') {
                        navigation.navigate('ProjectEvalTeam', { projectId });
                    } else {
                        Alert.alert('권한 없음', '평가 권한이 없습니다.');
                    }
                }}
            >
                <Text style={styles.evalBtnText}>작품 평가하기</Text>
            </TouchableOpacity>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    posterImg: { width: '100%', height: 260, resizeMode: 'cover', backgroundColor: '#eaeaea' },
    infoBox: { paddingHorizontal: 22, paddingTop: 14, paddingBottom: 6 },
    titleRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 3 },
    projectTitle: { fontSize: 23, fontWeight: 'bold', marginRight: 8 },
    medalIcon: { width: 24, height: 24, resizeMode: 'contain' },
    subInfoRow: { flexDirection: 'row', marginBottom: 9 },
    metaText: { fontSize: 14, color: '#546' },
    detailDesc: { fontSize: 15, paddingHorizontal: 22, color: '#222', marginBottom: 34, minHeight: 120, lineHeight: 21 },
    evalBtn: { backgroundColor: '#2359c4', margin: 22, borderRadius: 11, height: 47, alignItems: 'center', justifyContent: 'center' },
    evalBtnText: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});
