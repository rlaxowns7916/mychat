#!/usr/bin/env python3
"""
GitHub PR에서 테스트 파일의 [AI] 커밋을 검사하는 스크립트
"""

import os
import sys
import subprocess
import re
import json
from typing import List, Optional
import argparse


class GitHubPRAnalyzer:
    def __init__(self, pr_number: str, repository: str, github_token: str):
        self.pr_number = pr_number
        self.repository = repository
        self.github_token = github_token
        
    def get_pr_changed_files(self) -> List[str]:
        """PR의 변경된 파일 목록을 가져옵니다."""
        cmd = [
            "gh", "api", 
            f"/repos/{self.repository}/pulls/{self.pr_number}/files",
            "--jq", ".[].filename"
        ]
        
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            check=True,
            env={**os.environ, "GITHUB_TOKEN": self.github_token}
        )
        
        return [line.strip() for line in result.stdout.strip().split('\n') if line.strip()]
    
    def filter_test_files(self, files: List[str]) -> List[str]:
        """테스트 파일들만 필터링합니다."""
        test_files = []
        for file in files:
            if (
                '/test/' in file or 
                file.startswith('test/') or
                '/tests/' in file or
                file.startswith('tests/') or
                file.endswith('Test.kt') or
                file.endswith('Tests.kt') or
                'src/test/' in file
            ):
                test_files.append(file)
        return test_files
    
    def get_file_last_commit_message(self, file_path: str) -> Optional[str]:
        """파일의 마지막 커밋 메시지를 가져옵니다."""
        # PR의 base와 head SHA 가져오기
        pr_info_cmd = [
            "gh", "api", 
            f"/repos/{self.repository}/pulls/{self.pr_number}",
            "--jq", ".base.sha,.head.sha"
        ]
        
        pr_result = subprocess.run(
            pr_info_cmd,
            capture_output=True,
            text=True,
            check=True,
            env={**os.environ, "GITHUB_TOKEN": self.github_token}
        )
        
        lines = pr_result.stdout.strip().split('\n')
        base_sha = lines[0].strip('"')
        head_sha = lines[1].strip('"')
        
        # PR 범위 내에서 파일의 최신 커밋 메시지 가져오기
        cmd = [
            "git", "log", 
            f"{base_sha}..{head_sha}",
            "--pretty=format:%s",
            "-1",
            "--", file_path
        ]
        
        result = subprocess.run(cmd, capture_output=True, text=True)
        return result.stdout.strip() if result.returncode == 0 and result.stdout.strip() else None
    
    def check_ai_keyword(self, commit_message: str) -> bool:
        """커밋 메시지에 AI 키워드가 포함되어 있는지 확인합니다."""
        if not commit_message:
            return False
        
        ai_patterns = [r'\[AI\]', r'\[ai\]', r'\(AI\)', r'\(ai\)', r'AI:', r'ai:']
        return any(re.search(pattern, commit_message, re.IGNORECASE) for pattern in ai_patterns)
    
    def post_comment(self, message: str):
        """PR에 코멘트를 작성합니다."""
        cmd = [
            "gh", "pr", "comment", self.pr_number,
            "--body", message
        ]
        
        subprocess.run(
            cmd,
            env={**os.environ, "GITHUB_TOKEN": self.github_token},
            capture_output=True
        )
    
    def analyze_pr(self) -> bool:
        """PR을 분석하고 결과를 반환합니다."""
        # 변경된 파일 가져오기
        changed_files = self.get_pr_changed_files()
        if not changed_files:
            self.post_comment("✅ **AI 커밋 검사 완료**\n\n변경된 파일이 없습니다.")
            return False
        
        # 테스트 파일 필터링
        test_files = self.filter_test_files(changed_files)
        if not test_files:
            self.post_comment("✅ **AI 커밋 검사 완료**\n\n변경된 테스트 파일이 없습니다.")
            return False
        
        # AI 키워드 검사
        ai_violations = []
        for test_file in test_files:
            commit_message = self.get_file_last_commit_message(test_file)
            if commit_message and self.check_ai_keyword(commit_message):
                ai_violations.append((test_file, commit_message))
        
        # 결과 처리
        if ai_violations:
            comment = "❌ **AI 커밋 검사 실패**\n\n"
            comment += f"테스트 파일 중 [AI] 키워드가 포함된 커밋이 {len(ai_violations)}개 발견되었습니다:\n\n"
            
            for i, (file_path, message) in enumerate(ai_violations, 1):
                comment += f"{i}. **파일**: `{file_path}`\n"
                comment += f"   **커밋**: {message}\n\n"
            
            comment += "💡 **해결 방법**: 커밋 메시지에서 [AI] 키워드를 제거하거나, 수동으로 작성된 코드로 교체하세요."
            
            self.post_comment(comment)
            return True
        else:
            comment = "✅ **AI 커밋 검사 완료**\n\n"
            comment += f"검사된 테스트 파일 {len(test_files)}개의 커밋 메시지가 모두 안전합니다."
            
            self.post_comment(comment)
            return False


def main():
    parser = argparse.ArgumentParser(description='GitHub PR AI 커밋 검사기')
    parser.add_argument('--pr-number', required=True, help='Pull Request 번호')
    parser.add_argument('--repository', required=True, help='저장소 (owner/repo)')
    parser.add_argument('--github-token', required=True, help='GitHub 토큰')
    
    args = parser.parse_args()
    
    try:
        analyzer = GitHubPRAnalyzer(
            pr_number=args.pr_number,
            repository=args.repository,
            github_token=args.github_token
        )
        
        has_violations = analyzer.analyze_pr()
        sys.exit(1 if has_violations else 0)
        
    except Exception as e:
        sys.exit(1)


if __name__ == "__main__":
    main()
