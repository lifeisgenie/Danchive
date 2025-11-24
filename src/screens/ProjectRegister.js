import React, { useState } from 'react';
import { View, Text, TextInput, Button, TouchableOpacity, Alert, StyleSheet, Image } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { launchImageLibrary } from 'react-native-image-picker';

const API_URL = 'http://100.84.161.55:8080/api/v1/exhibits';

export default function ProjectRegister({ route, navigation }) {
    const { teamId, term } = route.params;

    const [title, setTitle] = useState('');
    const [intro, setIntro] = useState('');
    const [shortIntro, setShortIntro] = useState('');
    const [categories, setCategories] = useState('');
    const [poster, setPoster] = useState(null);
    const [loading, setLoading] = useState(false);

    // 이미지(포스터) 선택
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

    // 작품 등록 요청
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
        formData.append('term', String(term));
        formData.append('title', title);
        formData.append('intro', intro);
        formData.append('shortIntro', shortIntro);
        formData.append('categories', categories);

        formData.append('poster', {
            uri: poster.uri,
            type: poster.type || 'image/jpeg',
            name: poster.fileName || 'poster.jpg',
        });

        // PPT 선택/첨부 완전히 제거!

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
        <View style={styles.container}>
            <Text style={styles.header}>작품 등록</Text>
            <TextInput style={styles.input} placeholder="작품명(Title)" value={title} onChangeText={setTitle} />
            <TextInput style={styles.input} placeholder="소개글(Intro)" value={intro} onChangeText={setIntro} multiline />
            <TextInput style={styles.input} placeholder="짧은 소개(Short Intro)" value={shortIntro} onChangeText={setShortIntro} multiline />
            <TextInput style={styles.input} placeholder="카테고리(콤마 구분)" value={categories} onChangeText={setCategories} />
            <TouchableOpacity style={styles.fileBtn} onPress={pickPoster}>
                <Text style={styles.fileBtnText}>포스터 이미지 선택</Text>
            </TouchableOpacity>
            {poster && <Image source={{ uri: poster.uri }} style={styles.posterPreview} />}
            <Button title={loading ? "등록 중..." : "작품 등록"} onPress={handleSubmit} disabled={loading} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, padding: 22, backgroundColor: '#fff' },
    header: { fontSize: 22, fontWeight: 'bold', marginBottom: 18 },
    input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 6, padding: 12, marginBottom: 12, fontSize: 16 },
    fileBtn: { backgroundColor: '#023560', borderRadius: 8, padding: 12, marginBottom: 8, alignItems: 'center' },
    fileBtnText: { color: '#fff', fontWeight: '600' },
    posterPreview: { width: 100, height: 140, marginBottom: 8, borderRadius: 6 },
});
