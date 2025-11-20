import React, { useEffect, useState } from 'react';
import {
  Modal,
  TextInput,
  Alert,
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const menuItems = [
  { title: '설정' },
  { title: '비밀번호 재설정' },
  { title: '회원탈퇴' },
];

export default function MyPage({ navigation }) {
  const [userInfo, setUserInfo] = useState(null);
  const [showTeamModal, setShowTeamModal] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [isInviting, setIsInviting] = useState(false);
  const [teamInfo, setTeamInfo] = useState(null);   // 팀(멤버 등) 정보 별도 저장
  const [showInviteList, setShowInviteList] = useState(false);
  const [inviteList, setInviteList] = useState([]);
  const [selectedInviteId, setSelectedInviteId] = useState(null);
  const [isAcceptingInvite, setIsAcceptingInvite] = useState(false);
  useEffect(() => {
    const fetchUserAndTeamInfo = async () => {
      // 유저 정보
      const userStr = await AsyncStorage.getItem('currentUser');
      if (userStr) {
        try {
          setUserInfo(JSON.parse(userStr));
        } catch (e) {
          console.log('currentUser 파싱 에러:', e);
        }
      }
      // 팀 정보
      const accessToken = await AsyncStorage.getItem('userToken');
      if (accessToken) {
        try {
          const resp = await axios.get(`${API_BASE_URL}/teams/me`, {
            headers: { Authorization: `Bearer ${accessToken}` }
          });
          if (resp.data?.success && resp.data?.data) {
            setTeamInfo(resp.data.data);
            // userInfo(현재 로그인 유저 객체)에 teamId, team명 추가 저장
            if (userStr) {
              const prevUser = JSON.parse(userStr);
              const merged = { ...prevUser, team: resp.data.data.teamName, teamId: resp.data.data.teamId };
              setUserInfo(merged);
              await AsyncStorage.setItem('currentUser', JSON.stringify(merged));
            }
          }
        } catch (e) {
          // 유저가 팀이 없으면 404 등으로 실패할 수도 있으니 무시 가능
          console.log('팀 정보 조회 에러:', e);
        }
      }
    };
    fetchUserAndTeamInfo();
  }, []);

  const displayName = userInfo?.name || 'guest';

  // === 팀 생성 ===
  const handleCreateTeam = () => setShowTeamModal(true);

  const submitCreateTeam = async () => {
    if (!teamName.trim()) {
      Alert.alert('알림', '팀 이름을 입력해 주세요.');
      return;
    }

    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      if (!accessToken) {
        Alert.alert('오류', '로그인이 필요합니다.');
        return;
      }

      const response = await axios.post(
        `${API_BASE_URL}/teams`,
        { teamName },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );

      if (response.data?.success && response.data?.data) {
        const { teamId, teamName: newTeamName, roleInTeam } = response.data.data;
        const updatedUserInfo = {
          ...(userInfo || {}),
          team: newTeamName,
          teamId,
          roleInTeam,
        };
        setUserInfo(updatedUserInfo);
        await AsyncStorage.setItem('currentUser', JSON.stringify(updatedUserInfo));
        // 추가로 팀 정보도 새로 불러오기 (fetchTeamInfo)
        fetchUserAndTeamInfo();  // 위 useEffect 밖에서 따로 함수로 빼도 OK
        setShowTeamModal(false);
        setTeamName('');
      }
      else {
        Alert.alert('실패', response.data?.message || '팀 생성에 실패했습니다.');
      }
    } catch (e) {
      console.log('팀 생성 에러:', e);
      if (e.response) {
        Alert.alert('오류', e.response.data?.message || '서버 응답 오류');
      } else if (e.request) {
        Alert.alert('네트워크 오류', '요청은 전송됐지만 응답이 없습니다.');
      } else {
        Alert.alert('환경 오류', e.message);
      }
    }
  };

  // === 팀 초대 ===
  const handleInviteMember = () => setShowInviteModal(true);

  const submitInviteMember = async () => {
    setIsInviting(true);
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      if (!accessToken) {
        Alert.alert('오류', '로그인이 필요합니다.');
        setIsInviting(false);
        return;
      }

      const teamId = userInfo?.teamId;
      if (!teamId) {
        Alert.alert('오류', '팀이 없습니다. 먼저 팀을 생성해 주세요.');
        setIsInviting(false);
        return;
      }

      const response = await axios.post(
        `${API_BASE_URL}/teams/${teamId}/invites`,
        { email: inviteEmail },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );

      if (response.data?.success) {
        Alert.alert('성공', response.data.message || '초대가 전송되었습니다.');
        setShowInviteModal(false);
        setInviteEmail('');
      } else {
        Alert.alert('실패', response.data?.message || '초대에 실패했습니다.');
      }
    } catch (e) {
      console.log('초대 에러:', e);
      Alert.alert('오류', '네트워크 또는 서버 오류입니다.');
    } finally {
      setIsInviting(false);
    }
  };

  // === 초대 목록 불러오기 ===
  const fetchInviteList = async () => {
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      const resp = await axios.get(`${API_BASE_URL}/teams/invites/me`, {
        headers: { Authorization: `Bearer ${accessToken}` }
      });
      if (resp.data?.success && Array.isArray(resp.data.data)) {
        setInviteList(resp.data.data);
      } else {
        setInviteList([]);
      }
    } catch (e) {
      setInviteList([]);
      Alert.alert('초대 현황 오류', '초대 조회 중 네트워크 오류가 발생했습니다.');
    }
  };
  const handleInviteStatus = async () => {
    await fetchInviteList();
    setShowInviteList(true);
  };

  // === 초대 수락 ===
  const acceptInvite = async () => {
    if (!selectedInviteId) {
      Alert.alert('선택 없음', '먼저 초대를 선택하세요.');
      return;
    }
    setIsAcceptingInvite(true);
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      const resp = await axios.post(
        `${API_BASE_URL}/teams/invites/${selectedInviteId}/accept`,
        {},
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      if (resp.data?.success) {
        Alert.alert('수락 완료', resp.data.message || '초대를 수락했습니다.');
        setShowInviteList(false);
        setSelectedInviteId(null);
        // 필요 시 팀 정보 리로드 등 추가
      } else {
        Alert.alert('실패', resp.data?.message || '초대 수락에 실패했습니다.');
      }
    } catch (e) {
      Alert.alert('오류', '네트워크 또는 서버 오류입니다.');
    } finally {
      setIsAcceptingInvite(false);
    }
  };

  const handleLogout = async () => {
    try {
      console.log('[로그아웃] 버튼 누름');
      const accessToken = await AsyncStorage.getItem('userToken');
      console.log('[로그아웃] 토큰:', accessToken);
      if (!accessToken) {
        Alert.alert('로그아웃 실패', '액세스 토큰이 없습니다.');
        navigation.reset({
          index: 0,
          routes: [{ name: 'Auth' }],
        });
        return;
      }
      await axios.post(
        `${API_BASE_URL}/auth/logout`,
        {},
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      await AsyncStorage.multiRemove(['userToken', 'refreshToken', 'currentUser']);
      console.log('[로그아웃] API 호출 및 토큰 삭제 완료');
      Alert.alert('로그아웃 완료', '정상적으로 로그아웃되었습니다.', [
        {
          text: '확인',
          onPress: () => {
            console.log('[로그아웃] navigation.reset 시도');
            navigation.reset({
              index: 0,
              routes: [{ name: 'Auth' }],
            });
          }
        }
      ]);
    } catch (e) {
      console.log('Logout Error:', e);  // 이 로그 반드시 체크!
      Alert.alert('로그아웃 실패', '서버와 통신에 실패했습니다.');
      navigation.reset({
        index: 0,
        routes: [{ name: 'Auth' }],
      })
    }
  };


  return (
    <SafeAreaView style={styles.container}>

      <Modal
        visible={showTeamModal}
        animationType="slide"
        transparent
        onRequestClose={() => setShowTeamModal(false)}
      >
        <View style={{
          flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.3)'
        }}>
          <View style={{
            width: 300, backgroundColor: 'white', borderRadius: 10, padding: 20, alignItems: 'center'
          }}>
            <Text style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 14 }}>팀 이름을 입력하세요</Text>
            <TextInput
              style={{
                borderWidth: 1, borderColor: '#ccc', borderRadius: 6, width: '100%',
                padding: 10, marginBottom: 20
              }}
              placeholder="팀 이름"
              value={teamName}
              onChangeText={setTeamName}
            />
            <TouchableOpacity
              style={{ backgroundColor: '#023560', paddingHorizontal: 24, paddingVertical: 10, borderRadius: 6, marginBottom: 10 }}
              onPress={submitCreateTeam}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>팀 생성</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={{ padding: 8 }}
              onPress={() => setShowTeamModal(false)}
            >
              <Text style={{ color: 'gray' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Modal
        visible={showInviteModal}
        animationType="slide"
        transparent
        onRequestClose={() => setShowInviteModal(false)}
      >
        <View style={{
          flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.3)'
        }}>
          <View style={{
            width: 300, backgroundColor: 'white', borderRadius: 10, padding: 20, alignItems: 'center'
          }}>
            <Text style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 14 }}>초대할 팀원 이메일</Text>
            <TextInput
              style={{
                borderWidth: 1, borderColor: '#ccc', borderRadius: 6, width: '100%',
                padding: 10, marginBottom: 20
              }}
              placeholder="이메일"
              value={inviteEmail}
              onChangeText={setInviteEmail}
              keyboardType="email-address"
              autoCapitalize="none"
            />
            <TouchableOpacity
              style={{ backgroundColor: '#023560', paddingHorizontal: 24, paddingVertical: 10, borderRadius: 6, marginBottom: 10 }}
              onPress={submitInviteMember}
              disabled={isInviting}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>초대</Text>
            </TouchableOpacity>
            <TouchableOpacity style={{ padding: 8 }} onPress={() => setShowInviteModal(false)}>
              <Text style={{ color: 'gray' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Modal
        visible={showInviteList}
        transparent
        animationType="slide"
        onRequestClose={() => setShowInviteList(false)}
      >
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.2)' }}>
          <View style={{ width: 320, backgroundColor: 'white', borderRadius: 10, padding: 24 }}>
            <Text style={{ fontWeight: 'bold', marginBottom: 14 }}>내게 온 팀 초대 현황</Text>
            {inviteList.length === 0 ? (
              <Text style={{ marginBottom: 18 }}>받은 초대가 없습니다.</Text>
            ) : (
              inviteList.map(invite => (
                <TouchableOpacity
                  key={invite.inviteId}
                  style={{
                    borderWidth: 1,
                    borderColor: selectedInviteId === invite.inviteId ? '#023560' : '#888',
                    borderRadius: 8,
                    padding: 12,
                    marginBottom: 10,
                    backgroundColor: selectedInviteId === invite.inviteId ? '#e8f2ff' : '#f8f9fa',
                  }}
                  onPress={() => setSelectedInviteId(invite.inviteId)}
                >
                  <Text style={{ fontWeight: '600' }}>{invite.teamName}</Text>
                  <Text style={{ fontSize: 12, color: '#555' }}>상태: {invite.status}</Text>
                  <Text style={{ fontSize: 12, color: '#999' }}>초대일: {invite.invitedAt?.slice(0, 10)}</Text>
                </TouchableOpacity>
              ))
            )}
            <TouchableOpacity
              style={{
                marginTop: 10,
                backgroundColor: selectedInviteId ? '#023560' : '#b8c6cd',
                borderRadius: 6,
                paddingVertical: 10,
                alignItems: 'center'
              }}
              onPress={acceptInvite}
              disabled={!selectedInviteId || isAcceptingInvite}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>초대 수락</Text>
            </TouchableOpacity>
            <TouchableOpacity style={{ marginTop: 12, alignItems: 'center' }} onPress={() => setShowInviteList(false)}>
              <Text style={{ color: '#555' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
        <View style={styles.logoWrapper}>
          <Image source={require('./assets/logo.png')} style={styles.logo} />
        </View>

        <View style={styles.profileRow}>
          <Image
            source={
              userInfo && userInfo.profile
                ? userInfo.profile
                : require('./assets/profile.jpg') // 기본 이미지
            }
            style={styles.profileImage}
          />
          <View style={styles.infoSection}>
            <View style={styles.infoRow}>
              <Text style={styles.label}>이름</Text>
              <Text style={styles.value}>
                {userInfo && userInfo.name ? userInfo.name : 'guest'}
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>팀 이름</Text>
              <Text style={styles.value}>
                {teamInfo?.teamName ? teamInfo.teamName : '정보 없음'}
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>팀원</Text>
              <Text style={styles.value}>
                {teamInfo?.members
                  ? teamInfo.members.map(m => m.name).join(', ')
                  : '정보 없음'}
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>작품</Text>
              <Text style={styles.value}>
                {userInfo && userInfo.project ? userInfo.project : '정보 없음'}
              </Text>
            </View>
          </View>


        </View>
        <View style={styles.teamButtonRow}>
          <TouchableOpacity style={styles.teamButton} onPress={handleCreateTeam}>
            <Text style={styles.teamButtonText}>팀 생성</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.teamButton} onPress={handleInviteMember}>
            <Text style={styles.teamButtonText}>팀원 초대</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.teamButton} onPress={handleInviteStatus}>
            <Text style={styles.teamButtonText}>초대 현황</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.menuSection}>
          {menuItems.map((item, idx) => (
            <TouchableOpacity key={idx} style={styles.menuItem}>
              <Text style={styles.menuText}>{item.title}</Text>
            </TouchableOpacity>
          ))}

          {/* 로그아웃 */}
          <TouchableOpacity
            style={[styles.menuItem, { backgroundColor: '#f7eaea' }]}
            onPress={handleLogout}
          >
            <Text style={[styles.menuText, { color: 'red', fontWeight: 'bold' }]}>
              로그아웃
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  logoWrapper: { marginTop: 20, marginLeft: 18, marginBottom: 18 },
  logo: { width: 50, height: 18, resizeMode: 'contain' },
  profileRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: 28,
    marginBottom: 8,
  },
  profileImage: {
    width: 90,
    height: 110,
    borderRadius: 12,
    marginRight: 24,
    backgroundColor: '#ddd',
  },
  infoSection: { flex: 1 },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 18,
    borderBottomWidth: 1,
    borderColor: '#eee',
    paddingBottom: 2,
  },
  label: { width: 80, fontSize: 16, color: '#777' },
  value: { fontSize: 16, color: '#222', flex: 1 },
  menuSection: { marginTop: 28, paddingHorizontal: 28 },
  menuItem: {
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderColor: '#eee',
  },
  menuText: { fontSize: 16, color: '#444' },
  teamButtonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginVertical: 20,
    paddingHorizontal: 18,
    gap: 4,
  },
  teamButton: {
    flex: 1,
    minWidth: 100,
    marginHorizontal: 0,
    backgroundColor: '#023560',
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    height: 40,
  },
  teamButtonText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 14,
  },

});

