#!/usr/bin/env bash
log() {
  local level="${1:-INFO}"; shift
  local msg="$*"
  local ts; ts="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  case "$level" in
    INFO)  color="\033[1;34m"; cmd="::notice::" ;;
    WARN)  color="\033[1;33m"; cmd="::warning::" ;;
    ERROR) color="\033[1;31m"; cmd="::error::" ;;
    DEBUG) color="\033[1;36m"; cmd="::debug::" ;;
    *)     color="\033[0m";    cmd="::notice::" ;;
  esac
  echo -e "${color}[${level}] ${ts} - ${msg}\033[0m"
  echo "${cmd}${msg}"
}
