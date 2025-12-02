// UserEvaluationScreen.js
import React, { useState } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    StyleSheet,
    Alert,
    SafeAreaView,
} from 'react-native';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

export default function UserEvaluationScreen({ route, navigation }) {
    const { projectId, teamName } = route.params; // ProjectDetail에서 넘겨줌

    // 페이지 (1~4)
    const [page, setPage] = useState(1);

    // 설문 응답 상태
    const [q1, setQ1] = useState(null); // 1번: 팀 관련 질문
    const [q2, setQ2] = useState(null); // 2번: 아이디어/창의성
    const [q3, setQ3] = useState(null); // 3번: 완성도/주제 전달력

    const handleNext = () => {
        // 간단 유효성 검사 (필요 시 페이지별 검사)
        if (page === 1 && !q1) {
            Alert.alert('알림', '1번 문항에 응답해 주세요.');
            return;
        }
        if (page === 2 && !q2) {
            Alert.alert('알림', '2번 문항에 응답해 주세요.');
            return;
        }
        if (page === 3 && !q3) {
            Alert.alert('알림', '3번 문항에 응답해 주세요.');
            return;
        }
        if (page < 4) setPage(page + 1);
    };

    const handlePrev = () => {
        if (page > 1) setPage(page - 1);
    };

    const handleSubmit = async () => {
        if (!q1 || !q2 || !q3) {
            Alert.alert('알림', '모든 문항에 응답해 주세요.');
            return;
        }

        try {
            const token = await AsyncStorage.getItem('userToken');

            // TODO: 설문 저장 API가 있으면 여기서 먼저 호출
            // await axios.post(`${API_BASE_URL}/exhibits/${projectId}/vote`, { q1, q2, q3 }, { headers: ... });

            // 좋아요 증가 API 호출
            const resp = await axios.post(
                `${API_BASE_URL}/exhibits/${projectId}/like`,
                {},
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (resp.data?.success) {
                const likes = resp.data.data?.likes;
                Alert.alert('완료', `투표 및 좋아요가 반영되었습니다.\n현재 좋아요: ${likes}`);
                navigation.goBack(); // 또는 특정 완료 화면으로 이동
            } else {
                Alert.alert('오류', resp.data?.message || '요청 처리에 실패했습니다.');
            }
        } catch (e) {
            console.log('투표/좋아요 에러:', e.response?.data || e);
            Alert.alert('오류', e.response?.data?.message || '네트워크 오류');
        }
    };

    const renderScoreRow = (value, setter) => {
        // 1~5점 동그라미 선택 UI
        return (
            <View style={styles.optionsRow}>
                {[1, 2, 3, 4, 5].map(v => (
                    <TouchableOpacity
                        key={v}
                        style={[
                            styles.circle,
                            value === v && styles.circleSelected,
                        ]}
                        onPress={() => setter(v)}
                    >
                        <Text style={styles.circleText}>{v}</Text>
                    </TouchableOpacity>
                ))}
            </View>
        );
    };

    const renderPage = () => {
        if (page === 1) {
            return (
                <View style={styles.pageContent}>
                    <Text style={styles.pageTitle}>1/4</Text>
                    <Text style={styles.teamName}>{teamName}</Text>
                    <Text style={styles.question}>
                        1. 이번 전시회에서 가장 마음에 들었던 팀의 이름은 무엇인가요?
                    </Text>
                    {/* 여기서는 팀 이름을 고정으로 보여주기 때문에 선택 없이 q1을 1로 고정해도 되고,
              "그렇다/보통/그렇지 않다" 같은 응답으로 구성해도 됨 */}
                    {renderScoreRow(q1, setQ1)}
                </View>
            );
        }

        if (page === 2) {
            return (
                <View style={styles.pageContent}>
                    <Text style={styles.pageTitle}>2/4</Text>
                    <Text style={styles.teamName}>작품 평가</Text>
                    <Text style={styles.question}>
                        2. 작품의 아이디어와 창의성/독창성은 얼마나 뛰어났다고 생각하십니까?
                    </Text>
                    {renderScoreRow(q2, setQ2)}
                </View>
            );
        }

        if (page === 3) {
            return (
                <View style={styles.pageContent}>
                    <Text style={styles.pageTitle}>3/4</Text>
                    <Text style={styles.teamName}>작품 평가</Text>
                    <Text style={styles.question}>
                        3. 작품의 완성도와 주제 전달력이 잘 구현되었다고 생각하십니까?
                    </Text>
                    {renderScoreRow(q3, setQ3)}
                </View>
            );
        }

        // page === 4 (결과/요약)
        return (
            <View style={styles.pageContent}>
                <Text style={styles.pageTitle}>4/4</Text>
                <Text style={styles.teamName}>{teamName}</Text>
                <Text style={styles.summaryTitle}>응답 내용 확인</Text>
                <Text style={styles.summaryItem}>1번 응답: {q1}</Text>
                <Text style={styles.summaryItem}>2번 응답: {q2}</Text>
                <Text style={styles.summaryItem}>3번 응답: {q3}</Text>
                <Text style={styles.summaryDesc}>
                    위 내용이 맞다면 "최종 확인"을 눌러 투표를 완료해 주세요.
                </Text>
            </View>
        );
    };

    return (
        <SafeAreaView style={styles.container}>
            {/* 상단 X 버튼 자리 등 필요시 추가 */}
            {renderPage()}

            <View style={styles.bottomBar}>
                {page > 1 ? (
                    <TouchableOpacity style={styles.bottomLeft} onPress={handlePrev}>
                        <Text style={styles.bottomText}>이전 설문</Text>
                    </TouchableOpacity>
                ) : (
                    <View style={styles.bottomLeft} />
                )}

                {page < 4 ? (
                    <TouchableOpacity style={styles.bottomRight} onPress={handleNext}>
                        <Text style={styles.bottomText}>다음 설문</Text>
                    </TouchableOpacity>
                ) : (
                    <TouchableOpacity style={styles.bottomRight} onPress={handleSubmit}>
                        <Text style={styles.bottomText}>최종 확인</Text>
                    </TouchableOpacity>
                )}
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },
    pageContent: { flex: 1, paddingHorizontal: 24, paddingTop: 24 },
    pageTitle: { fontSize: 16, color: '#888', marginBottom: 8 },
    teamName: { fontSize: 22, fontWeight: 'bold', marginBottom: 24, color: '#0066CC' },
    question: { fontSize: 16, marginBottom: 24 },
    optionsRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 24 },
    circle: {
        width: 48,
        height: 48,
        borderRadius: 24,
        borderWidth: 1,
        borderColor: '#ccc',
        alignItems: 'center',
        justifyContent: 'center',
    },
    circleSelected: {
        backgroundColor: '#007AFF',
        borderColor: '#007AFF',
    },
    circleText: { color: '#000', fontSize: 16 },
    summaryTitle: { fontSize: 18, fontWeight: 'bold', marginTop: 16, marginBottom: 12 },
    summaryItem: { fontSize: 16, marginBottom: 4 },
    summaryDesc: { fontSize: 14, color: '#666', marginTop: 8 },
    bottomBar: {
        flexDirection: 'row',
        height: 56,
        backgroundColor: '#0052CC',
    },
    bottomLeft: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    bottomRight: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    bottomText: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
});
