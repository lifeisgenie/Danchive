import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, Image, ScrollView, ActivityIndicator, Alert, Dimensions } from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';
const { width: screenWidth } = Dimensions.get('window');
const POSTER_HEIGHT = Math.round(screenWidth * 1.2);

export default function ProjectDetail({ route, navigation }) {
    const { projectId } = route.params;
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDetail = async () => {
            setLoading(true);
            try {
                const resp = await axios.get(`${API_BASE_URL}/exhibits/${projectId}`);
                console.log("API 응답:", resp.data?.data);
                setDetail(resp.data?.data);
            } catch (e) {
                Alert.alert('서버 오류', e.response?.data?.message || '네트워크 오류');
                setDetail(null);
            }
            setLoading(false);
        };
        fetchDetail();
    }, [projectId]);
    useEffect(() => {
        const loadCurrentUser = async () => {
            try {
                const userStr = await AsyncStorage.getItem('currentUser');
                if (userStr) {
                    const parsed = JSON.parse(userStr);
                    setCurrentUser(parsed);
                }
            } catch (e) {
                console.log('currentUser 로드 에러:', e);
            }
        };
        loadCurrentUser();
    }, []);
    useEffect(() => {
        const fetchDetail = async () => {
            setLoading(true);
            try {
                const resp = await axios.get(`${API_BASE_URL}/exhibits/${teamName}`);
                console.log("API 응답:", resp.data?.data);
                setDetail(resp.data?.data);
            } catch (e) {
                Alert.alert('서버 오류', e.response?.data?.message || '네트워크 오류');
                setDetail(null);
            }
            setLoading(false);
        };
        fetchDetail();
    }, [teamName]);
    if (loading || !detail) {
        return <ActivityIndicator style={{ margin: 44 }} size="large" color="#123" />;
    }

    // 이미지 URL 조립 시, 중복 슬래시 문제 방지
    const imgUri = detail.thumbnailUrl
        ? `${API_BASE_URL.replace('/api/v1', '/files')}/${detail.thumbnailUrl}`
        : null;


    console.log('최종 이미지 URL:', imgUri);

    const teamMembers = Array.isArray(detail.members)
        ? detail.members.map(m => m.name).join(', ')
        : detail.teamName || '';

    const handleEvaluatePress = () => {
        const role = currentUser?.role;

        if (role === 'prof') {
            navigation.navigate('ProfEvaluation', { projectId, teamName });
        } else {
            navigation.navigate('UserEvaluation', { projectId, teamName });
        }
    };

    return (
        <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 40 }}>
            <View style={styles.logoWrap}>
                <Image source={require('./assets/logo.png')} style={styles.logo} resizeMode="contain" />
            </View>
            <View style={styles.posterWrap}>
                <Image
                    source={
                        imgUri
                            ? { uri: imgUri }
                            : require('./assets/img-placeholder.png')
                    }
                    style={styles.posterImg}
                    resizeMode="contain"
                    onError={e => {
                        console.log('이미지 로드 에러:', e.nativeEvent.error);
                    }}
                />
            </View>
            <View style={styles.infoBox}>
                <Text style={styles.projectTitle}>{detail.title}</Text>
                <Text style={styles.metaText}>
                    {detail.categories?.join(', ')}
                    {detail.categories?.length && teamMembers ? ' | ' : ''}
                    {teamMembers}
                </Text>
                <Text style={styles.detailDesc}>{detail.shortIntro || detail.intro || ''}</Text>
            </View>
            <TouchableOpacity style={styles.buttonWrap} onPress={handleEvaluatePress}>
                <Text style={styles.buttonText}>작품 평가하기</Text>
            </TouchableOpacity>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    logoWrap: { marginLeft: 16, marginTop: 30, marginBottom: 18 },
    logo: { width: 100, height: 25, marginTop: 10 },
    posterWrap: { width: '100%', height: POSTER_HEIGHT, backgroundColor: '#eaeaea', marginBottom: 10 },
    posterImg: { width: '100%', height: '100%', borderRadius: 18 },
    infoBox: { paddingHorizontal: 22, paddingTop: 18 },
    projectTitle: { fontSize: 23, fontWeight: 'bold', marginBottom: 8 },
    metaText: { fontSize: 14, color: '#546', marginBottom: 12 },
    detailDesc: { fontSize: 15, color: '#222', marginBottom: 34, minHeight: 80, lineHeight: 21 },
    buttonWrap: { marginHorizontal: 32, marginTop: 20, alignItems: 'center' },
    buttonText: { backgroundColor: '#295cae', fontSize: 15, color: 'white', padding: 15, borderRadius: 9, fontWeight: 'bold', width: '100%', textAlign: 'center' },
});
