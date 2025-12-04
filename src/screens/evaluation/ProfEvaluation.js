import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    StyleSheet,
    Alert,
    TextInput,
} from 'react-native';
import axios from 'axios';
import { SafeAreaView } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

export default function ProfEvaluationScreen({ route, navigation }) {
    const { projectId, teamName } = route.params;

    const [page, setPage] = useState(1);
    const [technicalScore, setTechnicalScore] = useState(null);   // 기술성
    const [impactScore, setImpactScore] = useState(null);         // 영향력/파급력
    const [creativityScore, setCreativityScore] = useState(null); // 창의성
    const [comment, setComment] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [token, setToken] = useState(null);

    useEffect(() => {
        const loadToken = async () => {
            const t = await AsyncStorage.getItem('userToken');
            setToken(t);
        };
        loadToken();
    }, []);

    const handlePrev = () => {
        if (page > 1) setPage(page - 1);
    };

    const handleNext = () => {
        if (page === 1 && !technicalScore) {
            Alert.alert('알림', '1번 문항에 응답해 주세요.');
            return;
        }
        if (page === 2 && !impactScore) {
            Alert.alert('알림', '2번 문항에 응답해 주세요.');
            return;
        }
        if (page === 3 && !creativityScore) {
            Alert.alert('알림', '3번 문항에 응답해 주세요.');
            return;
        }
        if (page < 4) setPage(page + 1);
    };

    const handleClose = () => {
        navigation.goBack();
    };

    const handleSubmit = async () => {
        if (!technicalScore || !impactScore || !creativityScore) {
            Alert.alert('알림', '모든 별점을 입력해 주세요.');
            return;
        }
        if (!token) {
            Alert.alert('오류', '로그인 정보가 없습니다.');
            return;
        }

        setIsSubmitting(true);

        try {
            const body = {
                exhibitId: projectId,
                technicalScore,
                impactScore,
                creativityScore,
                comment: comment.trim(),
            };

            const resp = await axios.post(
                `${API_BASE_URL}/votes/professor`,
                body,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (resp.data?.success) {
                Alert.alert(
                    '완료',
                    resp.data.message || '교수 평가가 저장되었습니다.',
                    [{ text: '확인', onPress: () => navigation.goBack() }],
                );
            } else {
                Alert.alert('오류', resp.data?.message || '평가 저장에 실패했습니다.');
            }
        } catch (e) {
            const msg = e.response?.data?.message || '네트워크 오류가 발생했습니다.';
            if (msg.includes('교수 권한')) {
                Alert.alert('오류', '교수 권한이 필요합니다.');
            } else if (msg.includes('별점은 1~5')) {
                Alert.alert('오류', '별점은 1~5 사이의 값만 입력할 수 있습니다.');
            } else {
                Alert.alert('오류', msg);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderProgress = () => {
        const ratio = page / 4; // 1~3 질문, 4 요약
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
                        <Text style={styles.ratingLabel}>{labels[idx]}</Text>
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
                        <Text style={styles.screenTitle}>교수 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        작품의 기술적 완성도와 제시된 목표의 구현 수준을 평가해 주십시오.
                    </Text>
                    {renderRatingRow(technicalScore, setTechnicalScore)}
                </View>
            );
        }

        if (page === 2) {
            return (
                <View style={styles.pageContent}>
                    {renderProgress()}
                    <View style={styles.headerRow}>
                        <Text style={styles.screenTitle}>교수 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        작품이 다루는 문제의 중요성과 그 해결 방안의 실용적 가치/파급력을 평가해 주십시오.
                    </Text>

                    {renderRatingRow(impactScore, setImpactScore)}
                </View>
            );
        }

        if (page === 3) {
            return (
                <View style={styles.pageContent}>
                    {renderProgress()}
                    <View style={styles.headerRow}>
                        <Text style={styles.screenTitle}>교수 평가</Text>
                        <TouchableOpacity onPress={handleClose}>
                            <Text style={styles.closeText}>X</Text>
                        </TouchableOpacity>
                    </View>

                    <Text style={styles.questionText}>
                        작품에 담긴 아이디어의 창의성과 혁신성(새로움의 정도)을 평가해 주십시오.
                    </Text>

                    {renderRatingRow(creativityScore, setCreativityScore)}
                </View>
            );
        }

        // page === 4 : 요약 + 코멘트
        return (
            <View style={styles.pageContent}>
                {renderProgress()}
                <View style={styles.headerRow}>
                    <Text style={styles.screenTitle}>교수 평가 요약</Text>
                    <TouchableOpacity onPress={handleClose}>
                        <Text style={styles.closeText}>X</Text>
                    </TouchableOpacity>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        1. 기술적 완성도
                    </Text>
                    <Text style={styles.summaryAnswer}>
                        {technicalScore ?? '-'} 점
                    </Text>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        2. 실용성 / 파급력
                    </Text>
                    <Text style={styles.summaryAnswer}>
                        {impactScore ?? '-'} 점
                    </Text>
                </View>

                <View style={styles.summaryBlock}>
                    <Text style={styles.summaryQuestion}>
                        3. 아이디어와 창의성 / 독창성
                    </Text>
                    <Text style={styles.summaryAnswer}>
                        {creativityScore ?? '-'} 점
                    </Text>
                </View>

                <View style={styles.commentBox}>
                    <Text style={styles.inputLabel}>코멘트</Text>
                    <TextInput
                        style={styles.commentInput}
                        placeholder="총평을 입력해 주세요."
                        value={comment}
                        onChangeText={setComment}
                        multiline
                        textAlignVertical="top"
                    />
                </View>

                <Text style={styles.summaryDesc}>
                    위 내용이 맞다면 "평가 완료"를 눌러 교수 평가를 저장해 주세요.
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
            label = isSubmitting ? '처리중...' : '평가 완료';
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
                    <Text style={styles.bottomRightText}>{label}</Text>
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
        marginBottom: 24,
        lineHeight: 22,
    },
    teamName: {
        fontSize: 18,
        fontWeight: '700',
        color: '#1f59b6',
        textAlign: 'right',
        marginBottom: 32,
    },

    ratingRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 8,
        marginBottom: 24,
    },

    ratingItem: {
        alignItems: 'center',   // 원과 텍스트를 수직 중앙 정렬
        width: 60,              // 5개가 균등하게 보이도록 적당한 너비
    },

    circle: {
        width: 44,
        height: 44,
        borderRadius: 22,
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



    ratingLabel: { fontSize: 11, color: '#666' },

    summaryBlock: {
        paddingVertical: 10,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    summaryQuestion: { fontSize: 13, color: '#555', marginBottom: 4 },
    summaryAnswer: {
        fontSize: 16,
        fontWeight: '700',
        color: '#1f59b6',
        textAlign: 'right',
    },

    commentBox: {
        marginTop: 16,
    },
    inputLabel: {
        fontSize: 13,
        fontWeight: '600',
        marginBottom: 4,
        color: '#333',
    },
    commentInput: {
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 8,
        minHeight: 80,
        paddingHorizontal: 10,
        paddingVertical: 8,
        fontSize: 14,
        backgroundColor: '#fff',
    },

    summaryDesc: {
        fontSize: 13,
        color: '#666',
        marginTop: 16,
        lineHeight: 18,
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
    bottomLeftText: { color: '#fff', fontSize: 14 },
    bottomRight: {
        flex: 1.4,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#0043a3',
    },
    bottomRightDisabled: { opacity: 0.7 },
    bottomRightText: { color: '#fff', fontSize: 15, fontWeight: '700' },
});

