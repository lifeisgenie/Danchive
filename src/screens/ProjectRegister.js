import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert, StyleSheet, Image, ScrollView } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { launchImageLibrary } from 'react-native-image-picker';

const API_URL = 'http://100.84.161.55:8080/api/v1/exhibits';
const CURRENT_TERM = '2025-2'; 

export default function ProjectRegister({ route, navigation }) {
    const { teamId, term } = route.params;
    const [title, setTitle] = useState('');
    const [intro, setIntro] = useState('');
    const [shortIntro, setShortIntro] = useState('');
    const [categories, setCategories] = useState('');
    const [poster, setPoster] = useState(null);
    const [loading, setLoading] = useState(false);

    const pickPoster = async () => {
        launchImageLibrary(
            { mediaType: 'photo', quality: 1 },
            (response) => {
                if (response.didCancel) return;
                if (response.errorCode) {
                    Alert.alert('이미지 선택 오류', response.errorMessage || '이미지 선택 중 오류가 발생했습니다.');
                    return;
                }
                if (response.assets && response.assets.length > 0) {
                    setPoster(response.assets[0]);
                }
            }
        );
    };

    const handleSubmit = async () => {
        if (!title.trim() || !intro.trim() || !shortIntro.trim() || !categories.trim()) {
            Alert.alert('입력 오류', '모든 필드를 채워 주세요.');
            return;
        }
        if (!poster) {
            Alert.alert('입력 오류', '포스터 이미지를 첨부해 주세요.');
            return;
        }
        setLoading(true);
        const accessToken = await AsyncStorage.getItem('userToken');
        const formData = new FormData();
        formData.append('teamId', String(teamId));
        formData.append('term', CURRENT_TERM);    // 무조건 현재 학기값으로!
        formData.append('title', title);
        formData.append('intro', intro);
        formData.append('shortIntro', shortIntro);
        formData.append('categories', categories);
        formData.append('poster', {
            uri: poster.uri,
            type: poster.type || 'image/jpeg',
            name: poster.fileName || 'poster.jpg',
        });

        try {
            const response = await axios.post(API_URL, formData, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'multipart/form-data',
                },
            });
            if (response.data.success) {
                Alert.alert('성공', response.data.message, [
                    { text: '확인', onPress: () => navigation.goBack() }
                ]);
            } else {
                Alert.alert('오류', response.data.message || '등록 실패');
            }
        } catch (e) {
            Alert.alert('서버 오류', e.response?.data?.message || '네트워크 오류');
        } finally {
            setLoading(false);
        }
    };

    return (
        <ScrollView contentContainerStyle={styles.container}>

            {/* 미리보기 영역 */}
            <View style={styles.previewWrapper}>
                <Image
                    source={
                        poster
                            ? { uri: poster.uri }
                            : require('./assets/img-placeholder.png')
                    }
                    style={styles.posterPreview}
                />
                <Text style={styles.previewName}>
                    {poster
                        ? (poster.fileName || poster.uri.split('/').pop())
                        : '이미지가 없습니다'}
                </Text>
            </View>

            <TouchableOpacity style={styles.fileBtn} onPress={pickPoster}>
                <Text style={styles.fileBtnText}>포스터 이미지 선택</Text>
            </TouchableOpacity>

            {/* 입력폼 */}
            <TextInput style={styles.input} placeholder="작품명(Title)" value={title} onChangeText={setTitle} maxLength={50} />
            <TextInput style={[styles.input, styles.longinput]} placeholder="소개글(Intro)" value={intro} onChangeText={setIntro} multiline maxLength={600} />
            <TextInput style={[styles.input, styles.multiInput]} placeholder="짧은 소개(Short Intro)" value={shortIntro} onChangeText={setShortIntro} multiline maxLength={200} />
            <TextInput style={styles.input} placeholder="카테고리(콤마 구분)" value={categories} onChangeText={setCategories} maxLength={80} />

            <TouchableOpacity
                style={[styles.fileBtn, styles.submitBtn, loading && { backgroundColor: '#b8c6cd' }]}
                onPress={handleSubmit}
                disabled={loading}
                activeOpacity={0.8}
            >
                <Text style={styles.fileBtnText}>{loading ? '등록 중...' : '작품 등록'}</Text>
            </TouchableOpacity>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: { flexGrow: 1, backgroundColor: '#f8f8fa', padding: 22, alignItems: 'center' },
    header: { fontSize: 22, fontWeight: 'bold', marginBottom: 18, marginTop: 12, alignSelf: 'flex-start' },
    previewWrapper: { width: '100%', alignItems: 'center', marginBottom: 8 },
    posterPreview: { width: '90%', height: 220, borderRadius: 12, marginBottom: 4, alignSelf: 'center', backgroundColor: 'transparent' },
    previewName: { fontSize: 13, color: '#666', marginBottom: 4, fontWeight: 'bold' },
    fileBtn: { backgroundColor: '#023560', borderRadius: 8, padding: 12, marginBottom: 18, alignItems: 'center', width: '100%' },
    fileBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 17, letterSpacing: 1 },
    input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, paddingHorizontal: 18, paddingVertical: 14, marginBottom: 14, fontSize: 17, width: '100%', backgroundColor: '#fafcff', fontWeight: 'bold' },
    multiInput: { minHeight: 60, textAlignVertical: 'top', paddingTop: 18 },
    longinput: { height: 200, textAlignVertical: 'top', paddingTop: 18 },
    submitBtn: { width: '100%', marginTop: 10, marginBottom: 18 },
});
