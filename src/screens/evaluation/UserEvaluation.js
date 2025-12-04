import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    StyleSheet,
    Alert,
    SafeAreaView,
    TextInput,
} from 'react-native';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';
const TERM = '2025-2';

export default function UserEvaluationScreen({ route, navigation }) {
    const { projectId, teamName: passedTeamName } = route.params;

    const [page, setPage] = useState(1);
    const [q2, setQ2] = useState(null); // 창의성
    const [q3, setQ3] = useState(null); // 완성도
    const [voterName, setVoterName] = useState('');
    const [voterContact, setVoterContact] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userToken, setUserToken] = useState(null);

    const teamName = passedTeamName || 'Danchive';

    useEffect(() => {
        const checkLoginStatus = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                setUserToken(token);
                setIsLoggedIn(!!token);
            } catch (e) {
                console.log('로그인 상태 확인 에러:', e);
                setIsLoggedIn(false);
            }
        };
        checkLoginStatus();
    }, []);

    const handleNext = () => {
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

    const handleClose = () => {
        navigation.goBack();
    };

    const handleSubmit = async () => {
        if (!q2 || !q3) {
            Alert.alert('알림', '모든 문항에 응답해 주세요.');
            return;
        }

        setIsSubmitting(true);

        try {
            const body = {
                exhibitId: projectId,
                favoriteTeamName: teamName,
                creativityScore: q2,
                completionScore: q3,
            };

            if (!isLoggedIn) {
                if (!voterName.trim() || !voterContact.trim()) {
                    Alert.alert('알림', '방문객 정보(이름, 연락처)를 입력해 주세요.');
                    setIsSubmitting(false);
                    return;
                }
                body.voterName = voterName.trim();
                body.voterContact = voterContact.trim();
            }

            const config =
                isLoggedIn && userToken
                    ? { headers: { Authorization: `Bearer ${userToken}` } }
                    : {};

            const resp = await axios.post(
                `${API_BASE_URL}/votes/popular?term=${TERM}`,
                body,
                config
            );

            if (resp.data?.success) {
                Alert.alert(
                    '완료',
                    resp.data.message || '인기상 투표가 완료되었습니다.',
                    [{ text: '확인', onPress: () => navigation.goBack() }]
                );
            } else {
                Alert.alert('오류', resp.data?.message || '투표 처리에 실패했습니다.');
            }
        } catch (e) {
            console.log('투표 에러:', e.response?.data || e);
            if (e.response?.status === 409) {
                Alert.alert(
                    '알림',
                    '이미 해당 학기에 투표하셨습니다. (참여팀은 학기당 1회만 가능)'
                );
            } else {
                Alert.alert(
                    '오류',
                    e.response?.data?.message || '네트워크 오류가 발생했습니다.'
                );
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderProgress = () => {
        const total = 4; // 1,2,3 질문 + 4 요약
        const ratio = page / total;

        return (
            <View style={styles.progressWrap}>
                <Text style={styles.progressText}>{page}/4</Text>
                <View style={styles.progressBarBg}>
                    <View style={[styles.progressBarFill, { flex: ratio }]} />
                    <View style={{ flex: 1 - ratio }} />
                </View>
            </View>
        );
    };

    const renderRatingRow = (value, setter) => {
        const labels = ['전혀 아니다', '아니다', '보통', '그렇다', '매우 그렇다'];

        return (
            <View style={styles.ratingRow}>
                {[1, 2, 3, 4, 5].map((v, idx) => (
                    <TouchableOpacity
                        key={v}
                        style={styles.ratingItem}
                        onPress={() => setter(v)}
                        disabled={isSubmitting}
                        activeOpacity={0.8}
                    >
                        <View
                            style={[
                                styles.circle,
                                value === v && styles.circleSelected,
                            ]}
                        >
                            <Text
                                style={[
                                    styles.circleText,
                                    value === v && styles.circleTextSelected,
                                ]}
                            >
                                {v}
                            </Text>
                        </View>
                        <Text
                            style={styles.ratingLabel}
                            numberOfLines={1}
                        >
                            {labels[idx]}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>
        );
    };




    const renderPage = () => {
        if (page === 1) {
            return (
                <View style={styles.pageContent}>
                    {renderProgress()}
                    <View style={styles.headerRow}>
                        <Text style={styles.screenTitle}>작품 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        이번 전시회에서 가장 마음에 들었던 팀의 이름은 무엇인가요?
                    </Text>

                    <View style={styles.teamAnswerRow}>
                        <Text style={styles.teamAnswer}>{teamName}</Text>
                    </View>
                </View>
            );
        }

        if (page === 2) {
            return (
                <View style={styles.pageContent}>
                    {renderProgress()}
                    <View style={styles.headerRow}>
                        <Text style={styles.screenTitle}>작품 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        작품의 아이디어와 창의성 / 독창성은
                        {'\n'}
                        얼마나 뛰어났다고 생각하십니까?
                    </Text>

                    {renderRatingRow(q2, setQ2)}
                </View>
            );
        }

        if (page === 3) {
            return (
                <View style={styles.pageContent}>
                    {renderProgress()}
                    <View style={styles.headerRow}>
                        <Text style={styles.screenTitle}>작품 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        작품의 완성도와 주제 전달력이
                        {'\n'}
                        잘 구현되었다고 생각하십니까?
                    </Text>

                    {renderRatingRow(q3, setQ3)}
                </View>
            );
        }

        // page === 4 요약
        return (
            <View style={styles.pageContent}>
                {renderProgress()}
                <View style={styles.headerRow}>
                    <Text style={styles.screenTitle}>인기상 투표</Text>
                    <TouchableOpacity onPress={handleClose}>
                        <Text style={styles.closeText}>X</Text>
                    </TouchableOpacity>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        1. 이번 전시회에서 가장 마음에 들었던 팀의 이름은 무엇인가요?
                    </Text>
                    <Text style={styles.summaryAnswer}>{teamName}</Text>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        2. 작품의 아이디어와 창의성 / 독창성은
                        {'\n'}
                        얼마나 뛰어났다고 생각하십니까?
                    </Text>
                    <Text style={styles.summaryAnswer}>
                        {q2 ? ['전혀 아니다', '아니다', '보통', '그렇다', '매우 그렇다'][q2 - 1] : '-'}
                    </Text>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        3. 작품의 완성도와 주제 전달력이
                        {'\n'}
                        잘 구현되었다고 생각하십니까?
                    </Text>
                    <Text style={styles.summaryAnswer}>
                        {q3 ? ['전혀 아니다', '아니다', '보통', '그렇다', '매우 그렇다'][q3 - 1] : '-'}
                    </Text>
                </View>

                {!isLoggedIn && (
                    <View style={styles.visitorInfo}>
                        <Text style={styles.visitorTitle}>방문객 정보</Text>
                        <Text style={styles.inputLabel}>이름</Text>
                        <TextInput
                            style={styles.inputField}
                            placeholder="이름을 입력해주세요"
                            value={voterName}
                            onChangeText={setVoterName}
                        />
                        <Text style={styles.inputLabel}>연락처</Text>
                        <TextInput
                            style={styles.inputField}
                            placeholder="010-1234-5678"
                            value={voterContact}
                            onChangeText={setVoterContact}
                            keyboardType="phone-pad"
                        />
                    </View>
                )}

                <Text style={styles.summaryDesc}>
                    위 내용이 맞다면 "설문 종료"를 눌러 투표를 완료해 주세요.
                </Text>
            </View>
        );
    };

    const renderBottomButton = () => {
        let label = '다음 설문';
        let onPress = handleNext;

        if (page === 3) {
            label = '최종 확인';
            onPress = () => setPage(4);
        } else if (page === 4) {
            label = '설문 종료';
            onPress = handleSubmit;
        }

        return (
            <View style={styles.bottomBar}>
                {page > 1 ? (
                    <TouchableOpacity
                        style={styles.bottomLeft}
                        onPress={handlePrev}
                        disabled={isSubmitting}
                    >
                        <Text style={styles.bottomLeftText}>이전 설문</Text>
                    </TouchableOpacity>
                ) : (
                    <View style={styles.bottomLeft} />
                )}

                <TouchableOpacity
                    style={[
                        styles.bottomRight,
                        isSubmitting && styles.bottomRightDisabled,
                    ]}
                    onPress={onPress}
                    disabled={isSubmitting}
                >
                    <Text style={styles.bottomRightText}>
                        {isSubmitting && page === 4 ? '처리중...' : label}
                    </Text>
                </TouchableOpacity>
            </View>
        );
    };

    return (
        <SafeAreaView style={styles.container}>
            {renderPage()}
            {renderBottomButton()}
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#fff' },

    pageContent: { flex: 1, paddingHorizontal: 24, paddingTop: 16 },

    headerRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 24,
    },
    screenTitle: { fontSize: 16, fontWeight: '600' },
    closeText: { fontSize: 18, color: '#555' },

    progressWrap: { marginBottom: 12 },
    progressText: { fontSize: 13, color: '#0070c9', marginBottom: 6 },
    progressBarBg: {
        height: 3,
        backgroundColor: '#e0e0e0',
        borderRadius: 2,
        flexDirection: 'row',
        overflow: 'hidden',
    },
    progressBarFill: {
        backgroundColor: '#0070c9',
        borderRadius: 2,
    },

    questionText: {
        fontSize: 16,
        fontWeight: '500',
        color: '#111',
        marginBottom: 32,
        lineHeight: 22,
    },

    teamAnswerRow: {
        alignItems: 'flex-end',
        marginTop: 8,
    },
    teamAnswer: {
        fontSize: 20,
        fontWeight: '700',
        color: '#1f59b6',
    },

    // ⬇ 원+텍스트 세트 (가로 5개)
    ratingRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 8,
        marginBottom: 24,
        paddingHorizontal: 4,
    },
    ratingItem: {
        width: 60,          // 세트 폭
        alignItems: 'center', // 원과 텍스트 수직 중앙 정렬
    },

    circle: {
        width: 40,
        height: 40,
        borderRadius: 20,
        borderWidth: 1.5,
        borderColor: '#d0d0d0',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f5f5f5',
    },
    circleSelected: {
        borderColor: '#0070c9',
        backgroundColor: '#0070c9',
    },
    circleText: { fontSize: 14, color: '#666' },
    circleTextSelected: { fontSize: 14, color: '#fff', fontWeight: '700' },

    ratingLabel: {
        marginTop: 6,
        fontSize: 11,
        color: '#666',
        textAlign: 'center',
    },

    summaryBlock: {
        paddingVertical: 12,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    summaryQuestion: {
        fontSize: 13,
        color: '#555',
        marginBottom: 6,
    },
    summaryAnswer: {
        fontSize: 16,
        fontWeight: '700',
        color: '#1f59b6',
        textAlign: 'right',
    },
    summaryDesc: {
        fontSize: 13,
        color: '#666',
        marginTop: 16,
        lineHeight: 18,
    },

    visitorInfo: {
        marginTop: 20,
        padding: 16,
        backgroundColor: '#f8f9fa',
        borderRadius: 10,
    },
    visitorTitle: {
        fontSize: 15,
        fontWeight: '700',
        color: '#0066CC',
        marginBottom: 8,
    },
    inputLabel: {
        fontSize: 13,
        fontWeight: '600',
        marginTop: 8,
        marginBottom: 4,
        color: '#333',
    },
    inputField: {
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 8,
        paddingHorizontal: 10,
        paddingVertical: 8,
        fontSize: 14,
        backgroundColor: '#fff',
    },

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
    bottomLeftText: {
        color: '#fff',
        fontSize: 14,
    },
    bottomRight: {
        flex: 1.4,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#0043a3',
    },
    bottomRightDisabled: {
        opacity: 0.7,
    },
    bottomRightText: {
        color: '#fff',
        fontSize: 15,
        fontWeight: '700',
    },
});
